plugins {
    alias(libs.plugins.android.application)
}

val envFile = rootProject.file(".env")
val envMap = mutableMapOf<String, String>()
if (envFile.exists()) {
    envFile.readLines().forEach { line ->
        val trimmed = line.trim()
        if (trimmed.isNotEmpty() && !trimmed.startsWith("#") && trimmed.contains("=")) {
            val (key, value) = trimmed.split("=", limit = 2)
            envMap[key.trim()] = value.trim()
        }
    }
}

android {
    namespace = "com.ptithcm.manga"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.ptithcm.manga"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"${envMap.getOrDefault("BASE_URL", "http://10.0.2.2:8080/api/")}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)

    // UI Components
    implementation(libs.viewpager2)
    implementation(libs.recyclerview)
    implementation(libs.cardview)
    implementation(libs.swiperefreshlayout)
    implementation(libs.fragment)

    // Image Loading
    implementation(libs.glide)
    implementation(libs.circleimageview)

    // Splash Screen
    implementation(libs.splashscreen)

    // Networking (Retrofit + OkHttp + Gson)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}