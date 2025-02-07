plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.winlowcustomer"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.winlowcustomer"
        minSdk = 24
        targetSdk = 35
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

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation("com.google.android.material:material:1.13.0-alpha10")
    implementation("com.google.android.material:material:1.5.0")
    // Circle Indicator (To fix the xml preview "Missing classes" error)
    implementation("me.relex:circleindicator:2.1.6")
    implementation("org.imaginativeworld.whynotimagecarousel:whynotimagecarousel:2.1.0")
    implementation("com.github.skydoves:expandablelayout:1.0.7")
    // steps
    // implementation("com.stepstone.stepper:material-stepper:4.3.1")
    // implementation("com.github.acefalobi:android-stepper:0.3.0")

    // progress bar
    implementation("com.github.mobven:MBAndroidProgressBar:1.0.0")

    // firebase
    implementation(platform("com.google.firebase:firebase-bom:33.8.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore")
//    implementation ("com.google.firebase:firebase-auth:22.3.1")
//    implementation ("com.google.android.gms:play-services-base:18.2.0")  // Ensure this is added

    // gson
    implementation ("com.google.code.gson:gson:2.12.1")
}