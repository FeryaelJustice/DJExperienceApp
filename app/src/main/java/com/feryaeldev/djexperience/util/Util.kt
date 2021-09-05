package com.feryaeldev.djexperience.util

import kotlin.reflect.full.memberProperties

// Object to Map
inline fun <reified T : Any> T.asMap(): Map<String, Any?> {
    val props = T::class.memberProperties.associateBy { it.name }
    return props.keys.associateWith { props[it]?.get(this) }
}