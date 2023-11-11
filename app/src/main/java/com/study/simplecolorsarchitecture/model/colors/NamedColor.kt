package com.study.simplecolorsarchitecture.model.colors

import java.io.Serializable

/**
 * Represents color data
 */
data class NamedColor(
    val id: Long,
    val name: String,
    val value: Int
): Serializable