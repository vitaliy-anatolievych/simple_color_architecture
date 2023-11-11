package com.study.core.contracts

import com.study.core.views.BaseScreen

/**
 * Контракт навігації
 */
interface Navigator {

    /**
     * Перейти на новий єкран
     */
    fun launch(screen: BaseScreen)

    /**
     * Повернення на попередній єкран і опціонально повертає параметри
     */
    fun goBack(result: Any? = null)

}
