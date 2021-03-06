-dontwarn
# 代码混淆压缩比，在0~7之间
-optimizationpasses 5
# 混合时不使用大小写混合，混合后的类名为小写
-dontusemixedcaseclassnames
# 在读取依赖的库文件时，不要略过那些非public类成员
-dontskipnonpubliclibraryclassmembers
# 指定不去忽略非公共库的类
-dontskipnonpubliclibraryclasses
# 不做预校验，preverify是proguard的四个步骤之一，去掉这一步能够加快混淆速度。
-dontpreverify
-verbose
# google推荐算法
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
# 保留注解、内部类、泛型、匿名类
-keepattributes *Annotation*,Exceptions,InnerClasses,Signature,EnclosingMethod
# 重命名抛出异常时的文件名称
-renamesourcefileattribute SourceFile
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
-dontwarn javax.annotation.**
# 保留本地native方法不被混淆
-keepclasseswithmembernames,allowshrinking class * {
native <methods>;
}
# 保留枚举类不被混淆
-keepclassmembers enum * {
public static **[] values();
public static ** valueOf(java.lang.String);
}
# 保留自定义类

使用了注解的部分

# 忽略数据库有关
-keep class com.litesuits.orm.**
-keepclassmembers class com.litesuits.orm.**{*;}
-keep enum com.litesuits.orm.**
-keepclassmembers enum com.litesuits.orm.**{*;}
-keep interface com.litesuits.orm.**
-keepclassmembers interface com.litesuits.orm.**{*;}
-keep class com.xcl.calculator.DataANet.**{*;}
# 忽略继承
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 保留配置文件
-printmapping mapping.txt