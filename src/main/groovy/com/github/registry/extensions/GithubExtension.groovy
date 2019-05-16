package com.github.registry.extensions

import org.gradle.api.Project
import org.gradle.api.plugins.BasePluginConvention

class GithubExtension {
    final Project project

    String repositoryOwner
    String token

    String[] configurations

    String[] publications

    GithubExtension(Project project) {
        this.project = project

        this.version = project.version
        this.groupId = project.group
        this.artifactId = project.convention.getPlugin(BasePluginConvention).archivesBaseName ?: project.name

        this.token = project.findProperty("github.token") ?: System.getenv("GITHUB_TOKEN")
        this.repositoryOwner = project.findProperty("github.owner") ?: System.getenv("GITHUB_OWNER")
    }
}
