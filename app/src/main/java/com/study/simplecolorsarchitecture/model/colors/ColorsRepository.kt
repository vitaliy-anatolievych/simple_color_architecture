package com.study.simplecolorsarchitecture.model.colors

import com.study.core.model.tasks.Tasks
import com.study.simplecolorsarchitecture.model.Repository
import kotlinx.coroutines.flow.Flow

typealias ColorListener = (NamedColor) -> Unit

/**
 * Repository interface example.
 *
 * Provides access to the available colors and current selected color.
 */
interface ColorsRepository : Repository {

    /**
     * Get the list of all available colors that may be chosen by the user.
     */
    fun getAvailableColors(): Tasks<List<NamedColor>>

    /**
     * Get the color content by its ID
     */
    fun getById(id: Long): Tasks<NamedColor>

    /**
     * Слухач поточного кольору
     */
    fun listenCurrentColor(): Flow<NamedColor>

    fun getCurrentColor(): Tasks<NamedColor>

    fun setCurrentColor(color: NamedColor): Flow<Int>

}