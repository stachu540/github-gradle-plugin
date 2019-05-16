[![Travis](https://img.shields.io/travis/com/stachu540/github-registry-plugin.svg)](https://travis-ci.com/stachu540/github-registry-plugin)
[![Releases](https://img.shields.io/github/tag/stachu540/github-registry-plugin.svg?color=sucess&label=release&logo=github)](https://github.com/stachu540/github-registry-plugin/releases)

# Getting Started

## Requirements

Gradle 4+ with Kotlin DSL

## Guide

#### Step 1: Create your [Personal Access Token](https://help.github.com/en/articles/creating-a-personal-access-token-for-the-command-line#creating-a-token) using [this site](https://github.com/settings/tokens)

It requires 2 scopes `read:packages` and `write:packages`. When your repository is private `repo` scope is required too.
For more about configuring for **Maven** use [this link](https://help.github.com/en/articles/configuring-maven-for-use-with-github-package-registry)

#### Step 2: Apply the plugin to your Gradle build script

###### Groovy `build.gradle`
```groovy
plugins {
    id "com.github.registry" version "0.1.0"
}
```

###### Kotlin `build.gradle.kts`
```kotlin
plugins {
    id("com.github.registry") version "0.1.0"
}
```

#### Step 3: Add the `github` configuration closure to your build file
```groovy
github {
    token = '<your personal access token here>' // is required to uploading your artifacts
    publications = ['MyPublication'] 
    repositoryOwner = '<repository owner>' // required if your repository owner is a different as your personal access token
}
publishing {
    publications {
        MyPublication(MavenPublication) {
            artifactId = archivesBaseName
            from(components.java)
            artifact(sourcesJar)
            artifact(javadocJar)
            artifact(kdocJar)
        }
    }
} 
```

###### Kotlin `build.gradle.kts`
```kotlin
github {
    token = "<your personal access token here>" // is required to uploading your artifacts
    publications = arrayOf("MyPublication")
    repositoryOwner = "<repository owner>" // required if your repository owner is a different as your personal access token
}

publishing {
    publications {
        register("MyPublication", MavenPublication::class) {
            artifactId = base.archivesBaseName
            artifact(tasks.named<Jar>("sourcesJar").get())
            artifact(tasks.named<Jar>("javadocJar").get())
            artifact(tasks.named<Jar>("kdocJar").get())
            from(components["java"])
        }
    }
}
```