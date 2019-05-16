package com.github.registry


import com.github.registry.task.GithubUploadTask
import org.gradle.api.Plugin
import org.gradle.api.Project

class GithubRepositoryPlugin implements Plugin<Project> {
    private static final String UPLOAD_DESCRIPTION = "Release your package to Github"
    private static final String TASK_GROUP = "github"

    @Override
    void apply(Project project) {
        def githubUpload = project.tasks.create(GithubUploadTask.NAME, GithubUploadTask.class)
        project.gradle.addListener(new ProjectEvaluateBuildListener(githubUpload as GithubUploadTask))
    }
}
