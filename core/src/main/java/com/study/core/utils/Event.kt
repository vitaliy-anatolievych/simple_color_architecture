package com.study.core.utils

import androidx.lifecycle.LiveData

/**
 * Ствоює "side effect". Використовується [LiveData]
 * для обгортки подій.
 */
class Event<T>(
    private val value: T
) {
    private var handled: Boolean = false

    fun getValue(): T? {
        if (handled) return null
        handled = true
        return value
    }

}