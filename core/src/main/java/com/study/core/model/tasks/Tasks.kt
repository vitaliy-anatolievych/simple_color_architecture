package com.study.core.model.tasks

import com.study.core.model.FinalResult

typealias TaskListener<T> = (FinalResult<T>) -> Unit

interface Tasks<T> {

    /**
     * Метод очікування результату синхронно.
     *
     * Блокування методу очікування та отримання результатів.
     * Згенерує виключення у випадку помилки.
     * @throws  [IllegalStateException], якщо завдання вже виконано
     */
    fun await(): T

    /**
     * Метод очікування результату асинхронно
     *
     * Неблокуючий метод для прослуховування результатів завдання.
     * Якщо завдання скасовано до завершення, слухач не викликається.
     *
     * Слухач викликається в основному потоці.
     * @throws [IllegalStateException], якщо завдання вже виконано.
     */
    fun enqueue(listener: TaskListener<T>)

    /**
     * Скасувати це завдання і видалити слухача, призначеного за допомогою [enqueue].
     */
    fun cancel()

}