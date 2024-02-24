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
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile
-keep class me.dkim19375.** { *; }
-keepclassmembers class me.dkim19375.** { *; }

-dontwarn kotlin.reflect.jvm.KCallablesJvm
-dontwarn me.mattstudios.config.SettingsHolder
-dontwarn me.mattstudios.config.SettingsManager
-dontwarn me.mattstudios.config.SettingsManagerBuilder
-dontwarn me.mattstudios.config.properties.Property
-dontwarn org.apache.commons.lang3.text.StrLookup
-dontwarn org.apache.commons.lang3.text.StrSubstitutor
-dontwarn org.apache.commons.text.StringSubstitutor
-dontwarn org.apache.commons.text.lookup.StringLookup
-dontwarn reactor.blockhound.integration.BlockHoundIntegration