# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class cu.thunder.ai.data.local.entity.** { *; }
-keep class cu.thunder.ai.domain.model.** { *; }

# MediaPipe Tasks GenAI 0.10.21
-keep class com.google.mediapipe.** { *; }
-keep class com.google.mediapipe.tasks.** { *; }
-keep class com.google.mediapipe.tasks.genai.** { *; }
-keep class com.google.mediapipe.framework.** { *; }
-dontwarn com.google.mediapipe.**
-dontwarn com.google.mediapipe.tasks.**

# Kotlin
-keepattributes *Annotation*
-keep class kotlinx.coroutines.** { *; }