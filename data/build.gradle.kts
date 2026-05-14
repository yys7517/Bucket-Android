import java.util.Properties
import kotlin.apply

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.bucket.data"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")

        val properties = Properties().apply {
            load(project.rootProject.file("local.properties").inputStream())
        }
        val baseUrl = properties.getProperty("BASE_URL", "http://localhost:8080/")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":domain")) // domain 모듈 의존

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    implementation(libs.play.services.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // ktor
    implementation(libs.bundles.ktor)

    // kotlinx-serialization
    implementation(libs.kotlinx.serialization.json)

    // timber
    implementation(libs.timber)


    // datastore
    implementation(libs.androidx.datastore.preferences)
}
