plugins {
    id("androidx.room")
    id("org.cadixdev.licenser")
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.android")

    kotlin("plugin.serialization")
}

android {
    namespace = "me.dkim19375.dictionary"
    compileSdk = 34

    defaultConfig {
        applicationId = "me.dkim19375.dictionary"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.10"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    license {
        this.header.set(rootProject.resources.text.fromFile("LICENSE"))
        include("**/*.kt")

        this.tasks {
            create("mainAndroid") { // doesn't auto-detect for some reason
                @Suppress("UnstableApiUsage")
                files.setFrom(project.android.sourceSets["main"].java.srcDirs)
            }
        }
    }
}

room {
    schemaDirectory("schemas/")
}

dependencies {

    implementation("com.google.android.gms:play-services-wearable:18.2.0")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.wear.compose:compose-foundation:1.3.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("androidx.wear.compose:compose-material:1.4.0-alpha04")
    implementation("androidx.compose.material:material-icons-extended:1.7.0-beta03")
    implementation("androidx.wear:wear-tooling-preview:1.0.0")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-cbor:1.6.3")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.12.0"))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("androidx.wear:wear-input:1.2.0-alpha02")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.work:work-runtime:2.9.0")
    implementation("io.github.dkim19375:dkimcore:1.5.0")
    implementation("com.google.android.horologist:horologist-compose-layout:0.6.5")
    implementation("com.google.android.horologist:horologist-compose-material:0.6.5")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.05.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}