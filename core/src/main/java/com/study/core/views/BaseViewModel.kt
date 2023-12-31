package com.study.core.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.study.core.model.ErrorResult
import com.study.core.model.PendingResult
import com.study.core.utils.Event
import com.study.core.model.Result
import com.study.core.model.SuccessResult
import com.study.core.model.tasks.TaskListener
import com.study.core.model.tasks.Tasks
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

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


    fun <T> into(stateFlow: MutableStateFlow<Result<T>>, block: suspend () -> T) {
        viewModelScope.launch {
            try {
                stateFlow.value = SuccessResult(block())
            } catch (e: Exception) {
                if (e !is CancellationException) stateFlow.value = ErrorResult(e)
            }
        }
    }

    /**
     * Flow розширення для заміни savedStateHandle.getLiveData()
     * !! зациклювання між viewModelScope не буде, бо MutableStateFlow нічого не робить
     * якщо значення однакові !!
     */
    fun <T> SavedStateHandle.getCustomStateFlow(key: String, initValue: T): MutableStateFlow<T> {
        // щоб не путати this@SavedStateHandle та this@BaseViewModel
        val savedStateHandle = this

        // Беремо по ключу значення
        val mutableFlow = MutableStateFlow(savedStateHandle[key] ?: initValue)

        // слухаемо mutableFlow
        viewModelScope.launch {
            // слухаемо що прийшло з mutableFlow та записуємо в savedStateHandle
            mutableFlow.collect {
                savedStateHandle[key] = it
            }
        }

        // слухаемо savedStateHandle
        viewModelScope.launch {
            savedStateHandle.getLiveData<T>(key).asFlow().collect {
                /**
                 * Як сзовні хтось оновить значення в savedStateHandle,
                 * то ми парралельно це теж перехопимо та сповістимо про зміни
                 */
                mutableFlow.value = it
            }
        }

        return mutableFlow
    }
}