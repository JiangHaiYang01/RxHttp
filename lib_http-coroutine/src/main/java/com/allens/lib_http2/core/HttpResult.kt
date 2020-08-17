package com.allens.lib_http2.core

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

sealed class HttpResult<out T : Any> {

    data class Success<out T : Any>(val data: T) : HttpResult<T>()
    data class Error(val throwable: Throwable) : HttpResult<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[throwable=$throwable]"
        }
    }

    suspend inline fun result(
        crossinline success: (T) -> Unit,
        crossinline error: (Throwable) -> Unit
    ) {
        when (this) {
            is Success -> {
                withContext(Dispatchers.Main){
                    success(data)
                }

            }
            is Error -> {
                withContext(Dispatchers.Main){
                    error(throwable)
                }
            }
        }


    }
}