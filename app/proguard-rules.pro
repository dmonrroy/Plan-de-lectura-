# kotlinx.serialization: conservar serializadores generados de los DTO del plan.
-keepclassmembers class com.planlectura.biblia90.domain.** {
    *** Companion;
    kotlinx.serialization.KSerializer serializer(...);
}
-keep,includedescriptorclasses class com.planlectura.biblia90.domain.**$$serializer { *; }
