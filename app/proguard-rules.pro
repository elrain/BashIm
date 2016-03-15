# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\programs\android_sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontwarn com.squareup.picasso.**
-keep class android.support.v7.widget.LinearLayoutManager { *; }
-keepclassmembers class ** {
    public void onEvent*(***);
}
-keep class org.jsoup.Jsoup {*;}
-keep class org.jsoup.nodes.Document
-keep class org.jsoup.nodes.Element
-keep class org.jsoup.select.Elements

-dontwarn java.lang.invoke.*