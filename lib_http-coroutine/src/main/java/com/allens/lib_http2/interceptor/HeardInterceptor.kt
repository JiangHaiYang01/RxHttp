package com.allens.lib_http2.interceptor

import android.util.Log
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpConfig
import okhttp3.Interceptor
import okhttp3.Request

//请求头
object HeardInterceptor {
    fun register(map: Map<String, String>): Interceptor {
        return Interceptor { chain: Interceptor.Chain ->
            val request = chain.request()
            val builder: Request.Builder = request.newBuilder()
            for ((key, value) in map.entries) {
                if (HttpConfig.isLog) {
                    Log.i(RxHttp.TAG, "http----> add heard [key]:$key [value]:$value ")
                    builder.addHeader(key, value)
                }
            }
            chain.proceed(builder.build())
        }
    }
}

