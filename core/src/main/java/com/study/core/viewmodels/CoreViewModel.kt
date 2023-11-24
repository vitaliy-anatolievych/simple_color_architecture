package com.study.core.viewmodels

import androidx.lifecycle.ViewModel
import com.study.core.contracts.Navigator
import com.study.core.contracts.UiActions
import com.study.core.model.PendingResult
import com.study.core.model.tasks.TaskListener
import com.study.core.model.tasks.Tasks
import com.study.core.navigator.NavigatorManager
import com.study.core.views.MutableLiveResult

open class CoreViewModel(
    val uiActions: UiActions,
    val navigatorManager: NavigatorManager
): ViewModel(),
    Navigator by navigatorManager,
    UiActions by uiActions {

    private val tasks = mutableSetOf<Tasks<*>>()

    override fun onCleared() {
        super.onCleared()
        navigatorManager.clear()
        tasks.forEach { it.cancel() }
        tasks.clear()
    }

    /**
     * Запускати завдання асинхронно, слухати його результат і
     * автоматично відписує слухача у випадку знищення вьюмоделі
     */
    fun <T> Tasks<T>.safeEnqueue(listener: TaskListener<T>? = null) {
        tasks.add(this)
        this.enqueue {
            tasks.remove(this)
            listener?.invoke(it)
        }
    }

    /**
     * Асинхронний запуск задачі та зіставлення її результату з вказаним
     * [liveResult].
     * Задача автоматично скасовується, якщо вьюмодель буде знищено.
     */
    fun <T> Tasks<T>.into(liveResult: MutableLiveResult<T>) {
        liveResult.value = PendingResult()
        this.safeEnqueue {
            liveResult.value = it
        }
    }
}