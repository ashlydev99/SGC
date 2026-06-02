# Mantener Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# Mantener Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class cu.thunder.ai.data.local.entity.** { *; }
-keep class cu.thunder.ai.domain.model.** { *; }

# Mantener MediaPipe
-keep class com.google.mediapipe.** { *; }
-dontwarn com.google.mediapipe.**

# Kotlin
-keepattributes *Annotation*
-keep class kotlinx.coroutines.** { *; }