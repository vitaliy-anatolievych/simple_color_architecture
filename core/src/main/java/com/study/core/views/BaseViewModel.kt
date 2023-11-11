package com.study.core.views

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import com.study.core.utils.Event

typealias LiveEvent<T> = LiveData<Event<T>>
typealias MutableLiveEvent<T> = MutableLiveData<Event<T>>

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