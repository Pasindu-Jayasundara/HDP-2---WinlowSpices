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

    // gson
    implementation ("com.google.code.gson:gson:2.12.1")

    // image load from url
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // refresh pull down
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")

    //okhttp
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // payhere
//    implementation("com.github.PayHereDevs:payhere-android-sdk:v3.0.17")
    implementation("com.github.PayHereDevs:payhere-android-sdk:v3.0.17")
    implementation("androidx.appcompat:appcompat:1.6.0") // ignore if you have already added
    implementation("com.google.code.gson:gson:2.8.0") // ignore if you have already added
    //pdf
    implementation("com.itextpdf:itext7-core:7.1.15")

    // map
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")


    // firestorage
    implementation("com.google.firebase:firebase-storage")

    // deepl translate
//    implementation(libs.deepl.java)
//    implementation("com.deepl.api:deepl-java:1.8.1")


}