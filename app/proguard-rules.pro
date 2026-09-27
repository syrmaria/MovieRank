# Gson
-keepattributes Signature,*Annotation*,InnerClasses,EnclosingMethod

-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Preserve DTO classes and their members
-keep class com.maria.movierank.feature.trending.data.model.** { *; }
-keep class com.maria.movierank.feature.trending.domain.model.** { *; }
-keep class com.maria.movierank.network.data.model.** { *; }
-keep class com.maria.movierank.network.domain.model.** { *; }