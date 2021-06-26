import Version.kotlin_version

plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.mavenPublish)
}
android {
    compileSdkVersion(Version.sdk_version)
//    buildToolsVersion "30.0.3"

    defaultConfig {
        minSdkVersion(Version.sdk_version)
        targetSdkVersion(Version.sdk_version)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "includes" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
    implementation("androidx.core:core-ktx:1.5.0")
    implementation("androidx.appcompat:appcompat:1.3.0")
    implementation("com.google.android.material:material:1.3.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    // Logging
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("com.github.tony19:logback-android:1.1.1-12")
    // Apache File Utils
    implementation("commons-io:commons-io:2.6")
    // leanback support
    implementation(Dependencies.leanback)
}
//
//task cleanBuildPublishLocal(type: GradleBuild) {
//    tasks = ["clean", "build", "publishToMavenLocal"]
//}
//
//task cleanBuildPublish(type: GradleBuild) {
//    tasks = ["clean", "build", "publish"]
//}
//
//task cleanBuildPublishLocally {
//    dependsOn("assembleRelease")
//    dependsOn("cleanBuildPublish")
//    tasks.findByName("cleanBuildPublish").mustRunAfter("assembleRelease")
//}
//
//task generateSourcesJar(type: Jar) {
//    from android.sourceSets.main.java.srcDirs
//    classifier "sources"
//}
//


// USING KOTLIN DSL
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("production") {
                groupId = "intdev-utilities"
                artifactId = "log-uc"
                version = "1.0.99"

                from(components.getByName("release"))
            }
        }
        repositories {
            maven {
                isAllowInsecureProtocol = true
                url = uri("http://localhost:8081/repository/intdev-common/")
                credentials {
                    username = "admin"
                    password = "1234"
                }
            }
        }
    }
}


// USING GROOVY
//publishing {
//    publications {
//        // Define a release named Production
//        UCProduction(MavenPublication) {
//            // User reference implementation"cn.com.jack:mavendemo:2.7.0-SNAPSHOT"
//            groupId = rootProject.ext.pomGroupID
//            artifactId = rootProject.ext.pomArtifactId
//            version = rootProject.ext.pomVersionName
//            // Must have this, otherwise the AAR package will not be uploaded
//            afterEvaluate { artifact(tasks.getByName("bundleReleaseAar")) }
//            // Upload the source so that the user can see the method comment
//            artifact generateSourcesJar
//        }
//    }
//
//    repositories {
//        // Define a maven warehouse
//        maven {
//            allowInsecureProtocol(true)
//            // There can be one and only one warehouse without specifying the name attribute, it will be implicitly set to Maven
//            // Judge the warehouse address based on versionName
//
//            def baseUrl = rootProject.ext.nexusBaseUrl
//            def versionName = rootProject.ext.pomVersionName
//            // using mixed type repo and don"t need this for now
////            def suffix = versionName.endsWith("SNAPSHOT") ? "-snapshot" : ""
//            def suffix = ""
//            url = baseUrl + suffix
//            println url
//            // Warehouse username and password
//            credentials {
//                username = rootProject.ext.nexusUsername
//                password = rootProject.ext.nexusPassword
//            }
//        }
//    }
//}