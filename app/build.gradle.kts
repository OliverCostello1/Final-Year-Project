plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}


android {

    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
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
    packaging {
        jniLibs {
            excludes += setOf("META-INF/DEPENDENCIES".toString(), "META-INF/DISCLAIMER".toString())
        }
        resources {
            excludes += setOf("META-INF/DEPENDENCIES".toString(), "META-INF/DISCLAIMER".toString())
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_14
        targetCompatibility = JavaVersion.VERSION_14
    }
    kotlinOptions {
        jvmTarget = "14"
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.appcompat)
    implementation(libs.core)
    implementation(libs.core.ktx)
    implementation(libs.recyclerview)
    implementation(libs.volley)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.functions)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.okhttp)
    implementation(libs.core.v494)
    implementation(libs.bcprov.jdk18on)
    implementation(libs.androidsvg.aar)
    implementation(libs.crypto)
    implementation(libs.contracts)
    implementation(libs.firebase.database)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.google.firebase.functions) // Firebase Functions library

    implementation(libs.security.crypto)
}
configurations.all {
    exclude(group = "org.bouncycastle", module = "bcprov-jdk15on")
}
