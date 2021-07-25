package com.mobcast.data.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

suspend fun <T> safeAPICall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): ResultState<T> {
    return withContext(dispatcher) {
        try {
            ResultState.Success(apiCall.invoke())
        } catch (e: Exception) {
            when (e) {
                is IOException -> {
                    Timber.e(e)
                    ResultState.NetworkError
                }
                is HttpException -> ResultState.HttpError(e.code(), e.message())
                else -> ResultState.GenericError(e.message ?: "")
            }
        }
    }
}