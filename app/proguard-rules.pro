
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }

-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

-keep class com.yourname.animetracker.data.** { *; }
-keep class com.yourname.animetracker.domain.** { *; }

-dontwarn okhttp3.**
-dontwarn okio.**

-keep class coil.** { *; }