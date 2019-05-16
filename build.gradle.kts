plugins {
    groovy
    `kotlin-dsl`
    `maven-publish`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.10.1"
    id("com.gorylenko.gradle-git-properties") version "2.0.0"
}

repositories {
    jcenter()
}

base.archivesBaseName = project.name

dependencies {
    implementation("com.squareup.okhttp3:okhttp:3.14.1")
    implementation("com.squareup.okhttp3:logging-interceptor:3.14.1")
}

gradlePlugin {
    plugins {
        create(project.name) {
            id = "com.github.registry"
            implementationClass = "GithubRepositoryPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/stachu540/github-registry-plugin"
    vcsUrl = "https://github.com/stachu540/github-registry-plugin.git"
    description = project.description
    tags = listOf("github", "github-registry", "publications")
    (plugins) {
        (project.name) {
            displayName = "Github Registry Plugin"
        }
    }
}

gitProperties {
    keys = listOf(
            "git.branch",
            "git.commit.id",
            "git.commit.id.abbrev",
            "git.commit.id.describe"
    )
    dateFormatTimeZone = "GMT"
    customProperty("application.name", name)
    customProperty("application.version", version)
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            artifactId = base.archivesBaseName
            from(components["java"])
            pom {
                url.set("https://github.com/stachu540/github-registry-plugin")
                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/stachu540/github-registry-plugin/issues")
                }
                ciManagement {
                    system.set("Travis-CI")
                    url.set("https://travis-ci.com/stachu540/github-registry-plugin")
                }
                inceptionYear.set("2018")
                developers {
                    developer {
                        id.set("stachu540")
                        name.set("Damian Staszewski")
                        url.set("https://github.com/stachu540")
                        timezone.set("Europe/Warsaw")
                    }
                }
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://github.com/stachu540/github-registry-plugin/blob/master/LICENCE.md")
                        distribution.set("repo")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/stachu540/github-registry-plugin.git")
                    developerConnection.set("scm:git:git@github.com:stachu540/github-registry-plugin.git")
                    url.set("https://github.com/stachu540/github-registry-plugin")
                }
                distributionManagement {
                    downloadUrl.set("https://github.com/stachu540/github-registry-plugin/releases")
                }
            }
        }
    }
}

tasks {
    withType<Wrapper> {
        gradleVersion = "5.4.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}