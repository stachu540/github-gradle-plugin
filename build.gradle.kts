plugins {
    groovy
    `kotlin-dsl`
    `java-gradle-plugin`
    id("com.gradle.plugin-publish") version "0.10.0"
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
            displayName = "Github Registry Plugin"
            description = "A plugin for publishing to Github Registry."
            implementationClass = "GithubRepositoryPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/stachu540/github-registry-plugin"
    vcsUrl = "https://github.com/stachu540/github-registry-plugin.git"
    tags = listOf("github", "registry", "maven", "publications")
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

tasks {
    withType<Wrapper> {
        gradleVersion = "5.4.1"
        distributionType = Wrapper.DistributionType.ALL
    }
}