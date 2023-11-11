package com.study.core.views

import java.io.Serializable

const val ARG_SCREEN = "ARG_SCREEN"

/**
 * Базовий клас для визначення аргументів екрану
 * Зверніть увагу, що всі поля всередині екрану
 * мають бути серіалізованими.
 */
interface BaseScreen : Serializable