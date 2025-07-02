plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

android {
  compileSdk = 34
  defaultConfig {
    applicationId = "com.tuempresa.socialmanager"
    minSdk = 21
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }
  // …
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.10")
  // …
}
