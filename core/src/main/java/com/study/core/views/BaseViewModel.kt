package com.study.core.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.study.core.utils.Event
import com.study.core.utils.Result

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

    /**
     * Додай це метод у дочірній клас, якщо хочешь
     * слухати результат з інших єкранів
     */
    open fun onResult(result: Any) {

    }
}