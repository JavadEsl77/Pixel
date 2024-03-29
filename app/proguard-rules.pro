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


-keep class android.support.v7.widget.** { *; }
-keep class androidx.appcompat.widget.** { *; }
-keep class * extends androidx.fragment.app.Fragment{}

-dontwarn androidx.**
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-keep class androidx.paging.PagedStorage
-keep class androidx.paging.PagingDataAdapter
-keep class androidx.paging.LoadStateAdapter
-dontwarn android.arch.util.paging.CountedDataSource
-dontwarn android.arch.persistence.room.paging.LimitOffsetDataSource


-keepnames class * extends java.io.Serializable
-keepnames class * extends android.os.Parcelable
-keepnames class androidx.navigation.fragment.NavHostFragment

################################## START Glide #################################
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
################################## END Glide ####################################

################################## START OkHttp 3 ###############################
-dontwarn okio.**
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
################################## END OkHttp 3 #################################


################################## START Gson #######
# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
-keep class com.google.gson.stream.** { *; }
-keep class com.google.**{*;}

# Application classes that will be serialized/deserialized over Gson
-keep class com.javadEsl.pixel.data.** { *; }
-keep class com.javadEsl.pixel.api.** { *; }


# Prevent proguard from stripping interface information from TypeAdapter, TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
################################### END Gson #########

################################### START ViewBinding ############################
-keep class * implements androidx.viewbinding.ViewBinding {
    public static *** bind(android.view.View);
    public static *** inflate(android.view.LayoutInflater);
}
################################### END ViewBinding ##############################

################################### START Retrofit ###############################
# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault
-keepattributes Exceptions

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
#
# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**
#
# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit
#
# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
#
# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
#-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>
#
# Keep generic signature of Call, Response (R8 full mode strips signatures from non-kept items).
-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
#
## With R8 full mode generic signatures are stripped for classes that are not
## kept. Suspend functions are wrapped in continuations where the type argument
## is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
################################### END Retrofit #################################

## Keep class names of Hilt injected ViewModels since their name are used as a multibinding map key.
#-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel

################################## START Pump ####################################
-keep class com.huxq17.download.** { *; }
################################## END Pump ######################################



##---------------Begin: proguard configuration for okio  ----------
# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*
##---------------End: proguard configuration for okio  ----------



##---------------Begin: proguard configuration for admob  ----------
# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

# The following rules are used to strip any non essential Google Play Services classes and method.

# For Google Play Services
-keep public class com.google.android.gms.ads.**{
   public *;
}

# For old ads classes
-keep public class com.google.ads.**{
   public *;
}

# For mediation
-keepattributes *Annotation*

# Other required classes for Google Play Services
# Read more at http://developer.android.com/google/play-services/setup.html
-keep class * extends java.util.ListResourceBundle {
   protected Object[][] getContents();
}

-keep public class com.google.android.gms.common.internal.safeparcel.SafeParcelable {
   public static final *** NULL;
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
   @com.google.android.gms.common.annotation.KeepName *;
}

-keepnames class * implements android.os.Parcelable {
   public static final ** CREATOR;
}
##---------------End: proguard configuration for admob  ----------



##---------------Begin: proguard configuration for tapsell  ----------
-keepclassmembers enum * { *; }
-keep class **.R$* { *; }
-keep interface ir.tapsell.sdk.NoProguard
-keep interface ir.tapsell.sdk.NoNameProguard
-keep class * implements ir.tapsell.sdk.NoProguard { *; }
-keep interface * extends ir.tapsell.sdk.NoProguard { *; }
-keep enum * implements ir.tapsell.sdk.NoProguard { *; }
-keepnames class * implements ir.tapsell.sdk.NoNameProguard { *; }
-keepnames class * extends android.app.Activity
-keep class ir.tapsell.plus.model.** { *; }
-keep class ir.tapsell.sdk.models.** { *; }

-keep class ir.tapsell.sdk.nativeads.TapsellNativeVideoAdLoader$Builder {*;}
-keep class ir.tapsell.sdk.nativeads.TapsellNativeBannerAdLoader$Builder {*;}

-keepclasseswithmembers class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keep interface ir.tapsell.plus.NoProguard
-keep interface * extends ir.tapsell.plus.NoProguard { *; }
-keep class * implements ir.tapsell.plus.NoProguard { *; }

##---------------End: proguard configuration for tapsell  ----------



##---------------Begin: proguard configuration for AppLovin  ----------

-dontwarn com.applovin.**
-keep class com.applovin.** { *; }
-keep class com.google.android.gms.ads.identifier.** { *; }

##---------------End: proguard configuration for AppLovin  ----------



-keep public class com.bumptech.glide.**

# For communication with AdColony's WebView
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

################################### START Webview #################
-keepattributes JavascriptInterface
-keep public class android.net.http.SslError
-keep public class android.webkit.WebViewClient
-keepclassmembers class android.webkit.WebView{
   public *;
}

-dontwarn android.webkit.WebView
-dontwarn android.net.http.SslError
-dontwarn android.webkit.WebViewClient
-dontwarn android.support.**
################################### END Webvieww #################

# TapsellPlus needs their formal names
-keepnames public class com.google.android.gms.ads.MobileAds
-keepnames public class com.unity3d.services.monetization.IUnityMonetizationListener
-keepnames public class com.adcolony.sdk.AdColony
-keepnames public class com.google.android.gms.ads.identifier.AdvertisingIdClient
-keepnames public class com.chartboost.sdk.Chartboost
-keepnames public class com.applovin.sdk.AppLovinSdkSettings