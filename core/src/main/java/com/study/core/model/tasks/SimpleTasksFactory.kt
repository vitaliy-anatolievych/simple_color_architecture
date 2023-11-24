package com.study.core.model.tasks

import android.os.Handler
import android.os.Looper
import com.study.core.model.ErrorResult
import com.study.core.model.FinalResult
import com.study.core.model.SuccessResult

private val handler = Handler(Looper.getMainLooper())

// Temp implementation for TasksFactory and Task
class SimpleTasksFactory: TasksFactory {
    override fun <T> async(body: TaskBody<T>): Tasks<T> {
        return SimpleTask(body)
    }

    class SimpleTask<T>(
        private val body: TaskBody<T>
    ) : Tasks<T> {

        private var thread: Thread? = null
        private var cancelled = false

        override fun await(): T = body()

        override fun enqueue(listener: TaskListener<T>) {
            thread = Thread {
                try {
                    val data = body()
                    publishResult(listener, SuccessResult(data))
                } catch (e: Exception) {
                    publishResult(listener, ErrorResult(e))
                }
            }.apply { start() }
        }

        override fun cancel() {
            cancelled = true
            thread?.interrupt()
            thread = null
        }

        private fun publishResult(listener: TaskListener<T>, result: FinalResult<T>) {
            handler.post {
                if (cancelled) return@post
                listener(result)
            }
        }
    }
}