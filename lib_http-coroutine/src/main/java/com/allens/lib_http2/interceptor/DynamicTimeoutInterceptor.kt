package com.allens.lib_http2.interceptor

import com.allens.lib_http2.tools.RequestBuilder
import com.allens.lib_http2.tools.RxHttpLogTool
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit


//动态设置接口请求超时时间
object DynamicTimeoutInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val headers = request.headers

        var connectTimeOut = false
        var writeTimeOut = false
        var readTimeOut = false
        var newChain = chain
        if (headers.size > 0) {

            headers.forEach {
                when (it.first) {
                    RequestBuilder.DYNAMIC_READ_TIME_OUT -> {
                        check(it.second)
                        readTimeOut = true
                        RxHttpLogTool.i("修改 read time out ${it.second.toInt()}")
                        newChain =
                            newChain.withReadTimeout(it.second.toInt(), TimeUnit.MILLISECONDS)
                    }
                    RequestBuilder.DYNAMIC_WRITE_TIME_OUT -> {
                        check(it.second)
                        writeTimeOut = true
                        RxHttpLogTool.i("修改 write time out ${it.second.toInt()}")
                        newChain =
                            newChain.withWriteTimeout(it.second.toInt(), TimeUnit.MILLISECONDS)
                    }
                    RequestBuilder.DYNAMIC_CONNECT_TIME_OUT -> {
                        check(it.second)
                        connectTimeOut = true
                        RxHttpLogTool.i("修改 connect time out ${it.second.toInt()}")
                        newChain =
                            newChain.withConnectTimeout(it.second.toInt(), TimeUnit.MILLISECONDS)
                    }
                }
            }
        }
        val newBuilder = request.newBuilder()
        if (readTimeOut) {
            newBuilder.removeHeader(RequestBuilder.DYNAMIC_READ_TIME_OUT)
        }
        if (connectTimeOut) {
            newBuilder.removeHeader(RequestBuilder.DYNAMIC_CONNECT_TIME_OUT)
        }
        if (writeTimeOut) {
            newBuilder.removeHeader(RequestBuilder.DYNAMIC_WRITE_TIME_OUT)
        }
        return newChain.proceed(newBuilder.build())
    }

    private fun check(timeout: String) {
        if (timeout.toLong() < 0) {
            throw Throwable("time out must over 0 ,please check DynamicTimeout")
        }
    }

}