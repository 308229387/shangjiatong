# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdk-windows/tools/proguard/proguard-android.txt
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
#缺省proguard 会检查每一个引用是否正确，
#但是第三方库里面往往有些不会用到的类，没有正确引用。如果不配置的话，系统就会报错。
-dontwarn
#指定代码的压缩级别
-optimizationpasses 2
#混淆时不会使用大小写混合
-dontusemixedcaseclassnames
#指定不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
#不预校验
-dontpreverify
#不优化输入的类文件
-dontoptimize
#不记录日志
-verbose
#混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-ignorewarnings
#不进行混淆保持原样
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

#libraryjars libs/alipaysdk.jar
#-libraryjars libs/MobileSecSdk.jar
#-libraryjars libs/utdid4all-1.0.4.jar
#-libraryjars libs/android-async-http-1.4.5.jar
#-libraryjars libs/libPay58SDK.jar

#保留所有的本地native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
#保护指定的类和类的成员，但条件是所有指定的类和类成员是要存在
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}
# 保护指定类的成员，如果此类受到保护他们会保护的更好
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
#保护指定的类文件和类的成员
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*; }
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*; }
#保护指定类的成员，如果此类受到保护他们会保护的更好
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}