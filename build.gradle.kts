// build.gradle.kts (raíz)
plugins {
  // Aplica false para no “aplicar” aquí, solo declarar versión
  id("com.android.application") version "8.13.0" apply false
  id("org.jetbrains.kotlin.android") version "1.8.10" apply false
  // Si tienes librerías o librerías de Android Library:
  // id("com.android.library") version "8.13.0" apply false
}


android {
    compileSdk 34

    defaultConfig {
        applicationId "com.gestionclub.padres"
        minSdk 21
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
    implementation 'androidx.fragment:fragment:1.7.1'
    implementation 'com.google.code.gson:gson:2.10.1'
    implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
}