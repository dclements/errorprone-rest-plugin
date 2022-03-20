import config.LibVersions
import net.ltgt.gradle.errorprone.errorprone

plugins {
    idea
    `java-library`

    id("com.diffplug.spotless")
    id("net.ltgt.errorprone") version "2.0.2"
}

repositories {
    google()
    mavenCentral()
}

allprojects {
    apply(plugin = "idea")
    apply(plugin = "java-library")
    apply(plugin = "net.ltgt.errorprone")

    apply(from = "$rootDir/spotless.gradle.kts")

    repositories {
        google()
        mavenCentral()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    dependencies {
        errorprone("com.google.errorprone:error_prone_core:${LibVersions.Build.errorprone}")
    }

    tasks.withType<JavaCompile>().configureEach {
        options.errorprone.disableWarningsInGeneratedCode.set(true)
        shouldRunAfter("spotlessJava")
        shouldRunAfter("spotlessApply")
    }

    tasks.withType<JavaCompile>().configureEach {
        options.isFork = true
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    tasks.withType<Test>().configureEach {
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2 + 1).takeIf { it > 1 } ?: 2
    }
}
