package com.vishwaagrotech.digitalhr.utils

sealed class EventHandler<out T> {
    class Success<out T>(val result: T) : EventHandler<T>()
    class Failure(val errorText: String) : EventHandler<Nothing>()
    object Loading : EventHandler<Nothing>()
    object Empty : EventHandler<Nothing>()
}
