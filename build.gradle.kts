// build.gradle.kts (raíz)
plugins {
  // Aplica false para no "aplicar" aquí, solo declarar versión
  id("com.android.application") version "8.2.2" apply false
  id("org.jetbrains.kotlin.android") version "1.8.10" apply false
  id("com.google.gms.google-services") version "4.4.0" apply false
  // Si tienes librerías o librerías de Android Library:
  // id("com.android.library") version "8.2.2" apply false
}