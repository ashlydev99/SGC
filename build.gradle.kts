plugins {
    // Plugin de Android y Kotlin, ajusta según tu proyecto
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}