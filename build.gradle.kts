// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("androidx.room") version "2.6.1" apply false
    id("org.cadixdev.licenser") version "0.6.1" apply false
    id("com.android.application") version "8.8.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false

    kotlin("plugin.serialization") version "1.9.22" apply false
}