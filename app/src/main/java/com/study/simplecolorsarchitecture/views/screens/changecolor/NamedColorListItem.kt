package com.study.simplecolorsarchitecture.views.screens.changecolor

import com.study.simplecolorsarchitecture.model.colors.NamedColor

/**
 * Represents list item for the color; it may be selected or not
 */
data class NamedColorListItem(
    val namedColor: NamedColor,
    val selected: Boolean
)