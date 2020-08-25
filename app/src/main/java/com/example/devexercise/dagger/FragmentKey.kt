package com.example.devexercise.dagger

import androidx.fragment.app.Fragment
import kotlin.reflect.KClass

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class FragmentKey(val clazz: KClass<out Fragment>)