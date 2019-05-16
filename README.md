[![Travis](https://img.shields.io/travis/com/stachu540/github-registry-plugin.svg)](https://travis-ci.com/stachu540/github-registry-plugin)
[![Releases](https://img.shields.io/github/tag/stachu540/github-registry-plugin.svg?color=sucess&label=release&logo=github)](https://github.com/stachu540/github-registry-plugin/releases)

# Getting Started

## Requirements

Gradle 4+ with Kotlin DSL

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