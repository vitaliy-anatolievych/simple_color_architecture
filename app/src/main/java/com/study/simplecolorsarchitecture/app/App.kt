package com.study.simplecolorsarchitecture.app

import android.app.Application
import com.study.core.contracts.ModelsProvider
import com.study.simplecolorsarchitecture.model.colors.InMemoryColorsRepository

class App: Application(), ModelsProvider {

    override val models: List<Any>
        get() = listOf(
            InMemoryColorsRepository()
        )
}