# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
# ── Kotlinx Serialization ──────────────────────────────────────
# Without these, @Serializable classes get their generated $$serializer
# stripped or renamed, and RemoteAppraisal/RemoteItem/RemoteClient etc.
# fail to (de)serialize at runtime — a release-only crash on every sync call.
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** {
    *** Companion;
}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.example.luxeval.**$$serializer { *; }
-keepclassmembers class com.example.luxeval.** {
    *** Companion;
}
-keepclasseswithmembers class com.example.luxeval.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# ── Room ───────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ── Koin ───────────────────────────────────────────────────────
# Koin resolves ViewModel constructors reflectively via params.get()/get();
# R8 renaming constructor params or stripping "unused" constructors breaks DI silently.
-keep class org.koin.** { *; }
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

# ── Ktor ───────────────────────────────────────────────────────
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { volatile <fields>; }
-dontwarn io.ktor.**
-dontwarn kotlinx.coroutines.debug.**

# ── Supabase-kt ────────────────────────────────────────────────
-keep class io.github.jan.supabase.** { *; }
-dontwarn io.github.jan.supabase.**

# ── kotlinx.datetime ───────────────────────────────────────────
-keepclassmembers class kotlinx.datetime.** { *; }

# ── Your own serialized / persisted models ──────────────────────
# These cross a serialization boundary (Room, Supabase JSON, or both) —
# field names/order matter, so don't let R8 touch them.
-keep class com.example.luxeval.data.remote.** { *; }
-keep class com.example.luxeval.data.database.entities.** { *; }
-keep class com.example.luxeval.domain.** { *; }

# ── Coil3 ──────────────────────────────────────────────────────
-dontwarn coil3.**