package com.study.simplecolorsarchitecture.app

import android.app.Application
import com.study.core.contracts.ModelsProvider
import com.study.core.model.tasks.SimpleTasksFactory
import com.study.simplecolorsarchitecture.model.colors.InMemoryColorsRepository

class App: Application(), ModelsProvider {

    private val tasksFactory = SimpleTasksFactory()

    private val dependency = listOf<Any>(
        tasksFactory,
        InMemoryColorsRepository(tasksFactory)
    )

    override val models: List<Any>
        get() = dependency
}