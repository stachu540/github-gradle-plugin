package com.github.registry.task

import com.github.registry.Github
import com.github.registry.extensions.GithubExtension
import com.github.registry.task.entity.Artifact
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.internal.AbstractTask
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.Upload
import org.gradle.plugins.signing.Signature

class GithubUploadTask extends AbstractTask {
    static String NAME = "githubUpload"
    GithubExtension extension

    def getUrl() {
        "https://maven.pkg.github.com/${extension.repositoryOwner}"
    }

    @TaskAction
    void githubUpload() {
        checkProperties()
        def configs = extension.configurations.collect {
            if (it instanceof CharSequence) {
                Configuration configuration = project.configurations.findByName(it)
                if (configuration != null) {
                    return collectArtifacts(configuration)
                } else {
                    logger.error("{}: Could not find configuration: {}.", path, it)
                }
            } else if (it instanceof Configuration) {
                return collectArtifacts(it)
            } else {
                logger.error("{}: Unsupported configuration type: {}.", path, it.class)
            }
            []
        }.flatten() as Artifact[]
        def publications = extension.publications.collect {
            if (it instanceof CharSequence) {

            } else if (it instanceof MavenPublication) {
                return collectArtifacts((Configuration) it)
            } else {
                logger.error("{}: Unsupported publication type: {}.", path, it.class)
            }
            []
        }.flatten() as Artifact[]

        configs.each { uploadArtifact(it) }
        publications.each { uploadArtifact(it) }
    }

    private void uploadArtifact(Artifact artifact) {
        def uri = "${url}/${artifact.path}"

        if (!artifact.file.exists()) {
            logger.error("Skipping upload for missing file '$artifact.file'.")
            return
        }
        artifact.file.with {
            def request = new Request.Builder()
                    .addHeader("Authorization", "Bearer ${extension.token}")
                    .post(RequestBody.create(MediaType.get("*/*"), artifact.file))
                    .url(uri)
                    .build()

            logger.warn("Uploading to ${url}...")
            try {
                Response res = Github.execute(request)
                if (res.successful) {
                    logger.warn("Uploaded to ${url}.")
                } else {
                    throw new GradleException("Could not upload to ${url}: [${res.code()}] ${res.message()}")
                }
            } catch (IOException ex) {
                throw new GradleException("Could not upload to ${url}: ${ex.localizedMessage}", ex)
            }
        }
    }

    private Artifact[] collectArtifacts(Configuration config) {
        String artifactId
        List<Artifact> artifacts = Arrays.asList()

        boolean pomArtifact = config.allArtifacts.findResults {
            if (!it.file.exists()) {
                logger.error("{}: file {} could not be found.", path, it.file.getAbsolutePath())
                return null
            }
            if (it.type == 'pom') {
                return true
            }
        }

        if (!pomArtifact) {
            Upload installTask = project.tasks.withType(Upload).findByName("install")
            if (!installTask) {
                logger.info("maven plugin was not applied, no pom will be uploaded.")
            } else {
                File pom = new File(project.convention.plugins['maven'].mavenPomDir, "pom-default.xml")
                if (pom.exists()) {
                    artifactId = Github.readArtifactIdFromPom(pom)
                    artifacts << new Artifact(
                            name: artifactId,
                            groupId: project.group,
                            version: project.version,
                            extension: 'pom',
                            type: 'pom',
                            file: pom
                    )
                } else {
                    logger.debug("Pom file " + pom.getAbsolutePath() + " doesn't exists.")
                }
            }
        }
        config.allArtifacts.findResults {
            if (!it.file.exists()) {
                logger.error("{}: file {} could not be found.", path, it.file.getAbsolutePath())
                return null
            }
            boolean signedArtifact = (it instanceof Signature)
            def signedExtension = signedArtifact ? it.toSignArtifact.getExtension() : null
            String name = artifactId == null ? it.name : artifactId
            artifacts << new Artifact(
                    name: name, groupId: project.group, version: project.version, extension: it.extension,
                    type: it.type, classifier: it.classifier, file: it.file, signedExtension: signedExtension
            )
        }.unique()

        artifacts
    }

    private void checkProperties() {
        if (extension.token == null) {
            project.logger.error("Define a Github Token")
            System.exit(1)
        }
        checkUserExist()
    }

    private void checkUserExist() {
        if (extension.repositoryOwner == null) {
            logger.warn("Repository Owner is not defined! Checking them via Token")
            extension.repositoryOwner = getUserByToken()
        }
    }

    private String getUserByToken() {
        try {
            return Github.getUser(extension.token).login
        } catch (IOException ex) {
            logger.error("Cannot obtain user!", ex)
            System.exit(1)
        }
    }
}
