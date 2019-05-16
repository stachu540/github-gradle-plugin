package com.github.registry


import com.github.registry.task.entity.User
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.gradle.internal.impldep.com.google.gson.Gson
import org.gradle.internal.impldep.org.apache.maven.model.Model
import org.gradle.internal.impldep.org.apache.maven.model.io.xpp3.MavenXpp3Reader
import org.gradle.internal.impldep.org.apache.maven.project.MavenProject

class Github {
    private static String getVERSION() {
        Properties props = new Properties()
        props.load(Github.class.classLoader.getResource("git.properties").openStream())
        props.get('application.version')
    }

    private static String getREVISION() {
        Properties props = new Properties()
        props.load(Github.class.classLoader.getResource("git.properties").openStream())
        props.get('git.commit.id.abbrev')
    }

    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .addInterceptor({
                it.proceed(it.request().newBuilder()
                        .addHeader("User-Agent", "github-registry-plugin/$VERSION-$REVISION")
                        .build())
            })

    static User getUser(String token) throws IOException {
        new Gson().fromJson(execute(new Request.Builder()
                .addHeader("Accept", "application/vnd.github.v3+json")
                .addHeader("Authorization", "Bearer $token")
                .url("https://api.github.com/v3/user")
                .get()
                .build()).body().charStream(), User)
    }

    static Response execute(Request request) throws IOException {
        httpClient.newCall(request).execute()
    }

    static String readArtifactIdFromPom(File pom) {
        FileReader reader = new FileReader(pom)
        MavenXpp3Reader mavenreader = new MavenXpp3Reader()
        Model model = mavenreader.read(reader)
        MavenProject project = new MavenProject(model)
        return project.getArtifactId()
    }
}
