package com.study.simplecolorsarchitecture.model.colors

import android.graphics.Color
import com.study.core.model.tasks.Tasks
import com.study.core.model.tasks.TasksFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Simple in-memory implementation of [ColorsRepository]
 */
class InMemoryColorsRepository(
    private val tasksFactory: TasksFactory
) : ColorsRepository {

    private var currentColor: NamedColor = AVAILABLE_COLORS[0]

    private val listeners = mutableSetOf<ColorListener>()

    override fun listenCurrentColor(): Flow<NamedColor> = callbackFlow {
        val listener: ColorListener = {
            /**
             * Особливість методу [trySend] у тому що він повертає [ChannelResult]
             * що дозволяє дізнатись результат виконання Flow: isSuccess, isFailure, isClosed.
             */
            trySend(it)
        }

        listeners.add(listener)

        /**
         * Оскільки Cold Flow завершується одразу як виконає свою роботу код усередині,
         * [awaitClose] зупиняє виконання коллебеку у собі, та очікує доки не завершиться
         * зовнішня корутина, після чого виконає код у собі перед завершенням зовнішньої корутини.
         */
        awaitClose {
            listeners.remove(listener)
        }
    }.buffer(Channel.CONFLATED) // віддає останній результат з буферу

    override suspend fun getAvailableColors(): List<NamedColor> = withContext(Dispatchers.IO) {
        delay(1000)
        return@withContext AVAILABLE_COLORS
    }

    override suspend fun getById(id: Long): NamedColor = withContext(Dispatchers.IO) {
        delay(100)
        return@withContext AVAILABLE_COLORS.first { it.id == id }
    }

    override fun getCurrentColor(): Tasks<NamedColor> = tasksFactory.async {
        Thread.sleep(1000)
        return@async currentColor
    }

    override fun setCurrentColor(color: NamedColor): Flow<Int> = flow {
        if (currentColor != color) {
            var progress = 0
            while (progress < 100) {
                progress += 2
                delay(30)
                emit(progress) // публікація результату
            }
            // код виконується після while
            currentColor = color
            listeners.forEach { it(color) }
        } else {
            emit(100)
        }
    }.flowOn(Dispatchers.IO) // зміна контексту для коду в коллбеку, за замовчуванням Main

    companion object {
        private val AVAILABLE_COLORS = listOf(
            NamedColor(1, "Red", Color.RED),
            NamedColor(2, "Green", Color.GREEN),
            NamedColor(3, "Blue", Color.BLUE),
            NamedColor(4, "Yellow", Color.YELLOW),
            NamedColor(5, "Magenta", Color.MAGENTA),
            NamedColor(6, "Cyan", Color.CYAN),
            NamedColor(7, "Gray", Color.GRAY),
            NamedColor(8, "Navy", Color.rgb(0, 0, 128)),
            NamedColor(9, "Pink", Color.rgb(255, 20, 147)),
            NamedColor(10, "Sienna", Color.rgb(160, 82, 45)),
            NamedColor(11, "Khaki", Color.rgb(240, 230, 140)),
            NamedColor(12, "Forest Green", Color.rgb(34, 139, 34)),
            NamedColor(13, "Sky", Color.rgb(135, 206, 250)),
            NamedColor(14, "Olive", Color.rgb(107, 142, 35)),
            NamedColor(15, "Violet", Color.rgb(148, 0, 211)),
        )
    }
}