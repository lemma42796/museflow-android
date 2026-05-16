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

#百度ocr
-keep class com.baidu.ocr.sdk.**{*;}
-dontwarn com.baidu.ocr.**

#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#https://docs.jiguang.cn/janalytics/client/android_guide#mavencentral-%E8%87%AA%E5%8A%A8%E9%9B%86%E6%88%90%E6%96%B9%E5%BC%8F
-keep class cn.jiguang.** { *; }
-keep class android.support.** { *; }
-keep class androidx.** { *; }
-keep class com.google.android.** { *; }