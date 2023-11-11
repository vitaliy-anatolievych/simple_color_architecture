package com.study.core.contracts

/**
 * Контракт на UI події
 */
interface UiActions {

    /**
     * Показ простого тост повідомлення
     */
    fun toast(message: String)

    /**
     * Отримати строку за його ідентифікатором
     */
    fun getString(messageRes: Int, vararg args: Any): String

}