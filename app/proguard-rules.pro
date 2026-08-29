# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Se preferisci non minimizzare/offuscare il codice per il progetto,
# minifyEnabled è già impostato su false in app/build.gradle, quindi
# queste regole non vengono attualmente applicate in fase di build.

# Mantiene gli attributi utili per il debug in caso di crash
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Gson: mantiene i campi dei modelli dati usati per il parsing JSON
-keep class com.drugdose.model.** { *; }
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
