plugins {
    java
    id("com.gradleup.shadow") version "8.3.0"
    id("io.freefair.lombok") version "8.4"
    `maven-publish`
}

allprojects {
    group = "dev.waterdog.waterdogpe"
    version = "2.0.4-SNAPSHOT"

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.opencollab.dev/maven-releases/") }
        maven { url = uri("https://repo.opencollab.dev/maven-snapshots/") }
        maven { url = uri("https://repo.waterdog.dev/main") }
    }
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
            }
        }
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
