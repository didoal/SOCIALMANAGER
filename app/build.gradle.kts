plugins {
  id("com.android.application")
  id("com.google.gms.google-services")
}

android {
  compileSdk = 34
  defaultConfig {
    applicationId = "com.gestionclub.padres"
    minSdk = 21
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }
  buildTypes {
    release {
      minifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("androidx.constraintlayout:constraintlayout:2.1.4")
  implementation("androidx.fragment:fragment:1.7.1")
  implementation("com.google.code.gson:gson:2.10.1")
  implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
}
