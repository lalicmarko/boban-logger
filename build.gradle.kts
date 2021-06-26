// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri(Dependencies.mavenUri)
        }
    }
    dependencies {
        classpath(Dependencies.androidPlugin)
        classpath(kotlin(Dependencies.kotlinPlugin, Version.kotlin_version))

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven {
            isAllowInsecureProtocol = true
            url = uri(Dependencies.nexusBaseUrl)
            credentials {
                username = "admin"
                password = "1234"
            }
        }
    }
}


tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}