package com.study.core.utils

typealias Mapper<Input, Output> = (Input) -> Output

sealed class Result<T> {

    fun <R> map(mapper: Mapper<T, R>? = null): Result<R> = when (this) {
        is ErrorResult -> ErrorResult(this.exception)
        is PendingResult -> PendingResult()
        is SuccessResult -> {
            if (mapper == null) throw IllegalStateException("Mapper should not be NULL for succes result")
            SuccessResult(mapper(this.data))
        }
    }
}

class PendingResult<T> : Result<T>()

class SuccessResult<T>(
    val data: T
) : Result<T>()

class ErrorResult<T>(
    val exception: Exception
) : Result<T>()


fun <T> Result<T>?.takeSuccess(): T? {
    return if (this is SuccessResult)
        this.data
    else
        null
}