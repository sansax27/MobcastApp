package com.mobcast.data.utils

sealed class UIState<out T> {
    object Empty:UIState<Nothing>()
    object Loading:UIState<Nothing>()
    data class Failure<T>(val message:String):UIState<T>()
    data class Success<T>(val data:T):UIState<T>()
}
