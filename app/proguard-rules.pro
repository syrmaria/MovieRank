# Gson
-keepattributes Signature,*Annotation*,InnerClasses,EnclosingMethod

#-keep class com.google.gson.reflect.TypeToken { *; }
#-keep class * extends com.google.gson.reflect.TypeToken
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Preserve DTO classes and their members
-keep class com.maria.movierank.feature.trending.data.model.** { *; }
-keep class com.maria.movierank.feature.trending.domain.model.** { *; }
-keep class com.maria.movierank.core.network.data.model.** { *; }
-keep class com.maria.movierank.core.network.domain.model.** { *; }