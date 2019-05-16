package com.github.registry

import com.github.registry.extensions.GithubExtension
import com.github.registry.task.GithubUploadTask
import org.gradle.BuildAdapter
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.publish.Publication
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.Upload

class ProjectEvaluateBuildListener extends BuildAdapter implements ProjectEvaluationListener {
    private GithubUploadTask githubUpload

    ProjectEvaluateBuildListener(GithubUploadTask githubUpload) {
        this.githubUpload = githubUpload
        this.githubUpload.extension = githubUpload.project.extensions.create("github", GithubExtension, githubUpload.project)
    }

    @Override
    void beforeEvaluate(Project project) {}

    @Override
    void afterEvaluate(Project project, ProjectState state) {
        if (githubUpload.extension.configurations?.length) {
            githubUpload.extension.configurations.each {
                def configuration = githubUpload.project.configurations.findByName(it)
                if (!configuration) {
                    githubUpload.project.logger.warn "Configuration ${it} specified but does not exist in project {}.",
                            githubUpload.project.path
                } else {
                    githubUpload.dependsOn(configuration.allArtifacts)
                }
            }
            Upload installTask = githubUpload.project.tasks.withType(Upload)?.findByName('install')
            if (installTask) {
                githubUpload.dependsOn(installTask)
            } else {
                githubUpload.project.logger.warn "Configuration(s) specified but the install task does not exist in project {}.",
                        githubUpload.project.path
            }
        }
        if (githubUpload.extension.publications?.length) {
            def publicationExt = githubUpload.project.extensions.findByType(PublishingExtension)
            if (!publicationExt) {
                githubUpload.project.logger.warn "Publications(s) specified but no publications exist in project {}.",
                        githubUpload.project.path
            } else {
                githubUpload.extension.publications.each {
                    Publication publication = publicationExt?.publications?.findByName(it)
                    if (!publication) {
                        githubUpload.project.logger.warn 'Publication {} not found in project {}.', it, githubUpload.project.path
                    } else if (publication instanceof MavenPublication) {
                        def taskName =
                                "publish${it[0].toUpperCase()}${it.substring(1)}PublicationToMavenLocal"
                        githubUpload.dependsOn(taskName)
                    } else {
                        githubUpload.project.logger.warn "{} can only use maven publications - skipping {}.",
                                githubUpload.path, publication.name
                    }
                }
            }
        }
    }
}
