package com.mobcast.data.utils


sealed class ResultState<out T> {
    data class Success<out T>(val data:T): ResultState<T>()
    object NetworkError: ResultState<Nothing>()
    data class GenericError(val message:String): ResultState<Nothing>()
    data class HttpError(val code:Int, val message: String): ResultState<Nothing>()
}
