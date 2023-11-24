package com.study.core.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.core.model.PendingResult
import com.study.core.utils.Event
import com.study.core.model.Result
import com.study.core.model.tasks.TaskListener
import com.study.core.model.tasks.Tasks

typealias LiveEvent<T> = LiveData<Event<T>>
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>

/**
 * Альтернативні типи для типів LiveData з Result
 */
typealias LiveResult<T> = LiveData<Result<T>>
typealias MutableLiveResult<T> = MutableLiveData<Result<T>>
typealias MediatorLiveResult<T> = MediatorLiveData<Result<T>>

/**
 * Базовий клас для усіх [ViewModel]
 */
open class BaseViewModel: ViewModel() {

    private val tasks = mutableSetOf<Tasks<*>>()

    /**
     * Додай це метод у дочірній клас, якщо хочешь
     * слухати результат з інших єкранів
     */
    open fun onResult(result: Any) {

    }

    override fun onCleared() {
        super.onCleared()
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