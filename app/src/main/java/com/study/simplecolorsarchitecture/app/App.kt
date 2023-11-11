package com.study.simplecolorsarchitecture.app

import android.app.Application
import com.study.core.contracts.ModelsProvider
import com.study.simplecolorsarchitecture.model.colors.InMemoryColorsRepository

class App: Application(), ModelsProvider {

    private val dependency = listOf<Any>(
        InMemoryColorsRepository()
    )

    override val models: List<Any>
        get() = dependency
}