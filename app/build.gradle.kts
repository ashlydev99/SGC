plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "cu.thunder.ai"
    compileSdk = 34

    defaultConfig {
        applicationId = "cu.thunder.ai"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        
        // Configurar NDK para llama.cpp
        ndk {
            abiFilters += listOf("arm64-v8a", "armeabi-v7a", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.5"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/NOTICE.md"
        }
        // Para librerías nativas de llama.cpp
        jniLibs {
            useLegacyPackaging = true
        }
    }
    
    // Directorio de librerías nativas
    sourceSets {
        getByName("main") {
            jniLibs.srcDirs("src/main/jniLibs")
        }
    }
}

dependencies {
    // =============================================
    // CORE ANDROID
    // =============================================
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation("androidx.activity:activity-ktx:1.8.1")

    // =============================================
    // COMPOSE
    // =============================================
    implementation(platform("androidx.compose:compose-bom:2023.10.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.runtime:runtime-livedata")

    // =============================================
    // NAVIGATION
    // =============================================
    implementation("androidx.navigation:navigation-compose:2.7.5")

    // =============================================
    // VIEWMODEL
    // =============================================
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // =============================================
    // ROOM (Base de datos)
    // =============================================
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // =============================================
    // COROUTINES
    // =============================================
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    // =============================================
    // JSON
    // =============================================
    implementation("com.google.code.gson:gson:2.10.1")

    // =============================================
    // DATASTORE (Preferencias)
    // =============================================
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // =============================================
    // MATERIAL DESIGN
    // =============================================
    implementation("com.google.android.material:material:1.10.0")

    // =============================================
    // 🧠 MOTOR 1: MediaPipe Tasks GenAI (.task)
    // =============================================
    implementation("com.google.mediapipe:tasks-genai:0.10.8")

    // =============================================
    // 🦙 MOTOR 2: llama.cpp - Wrapper Java (.gguf)
    // =============================================
    // Opción A: Usar wrapper de terceros (recomendado para empezar)
    // implementation("com.github.ggerganov:llama.cpp:master-SNAPSHOT")
    
    // Opción B: android-llama.cpp (wrapper más ligero)
    // implementation("com.github.mobile-ai:android-llama:1.0.0")
    
    // Opción C: Compilar tu propio .so (máximo control)
    // Los archivos .so van en:
    // app/src/main/jniLibs/arm64-v8a/libllama.so
    // app/src/main/jniLibs/armeabi-v7a/libllama.so
    // app/src/main/jniLibs/x86_64/libllama.so

    // =============================================
    // TESTING
    // =============================================
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.10.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}