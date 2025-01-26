plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.aventerashoe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.aventerashoe"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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


    implementation(libs.core.ktx)
    implementation(libs.appcompat.v170)
    implementation(libs.firebase.auth.v2202) // Add Firebase Auth dependency
    implementation(libs.material.v190)
    implementation (libs.firebase.database.v2030)



    implementation (libs.glide)
    implementation(libs.volley)
    implementation(libs.gridlayout)
    annotationProcessor (libs.compiler)


    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation(libs.material.v1110)


}

apply(plugin = "com.google.gms.google-services")