buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.5.1")
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
}