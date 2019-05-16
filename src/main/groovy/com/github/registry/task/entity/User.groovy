package com.github.registry.task.entity

import org.gradle.internal.impldep.com.google.gson.annotations.SerializedName

class User {
    String login
    @SerializedName("html_url")
    String url
    String name
    Date createdAt

    String getLogin() {
        return login
    }

    String getUrl() {
        return url
    }

    String getName() {
        return name
    }

    Date getCreatedAt() {
        return createdAt
    }
}
