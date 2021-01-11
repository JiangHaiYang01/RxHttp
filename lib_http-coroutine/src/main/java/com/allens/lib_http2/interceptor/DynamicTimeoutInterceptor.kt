package com.allens.lib_http2.interceptor

import com.allens.lib_http2.tools.DynamicHeard
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

        var newChain = chain
        if (headers.size > 0) {


            val readTimeOut = headers[DynamicHeard.DYNAMIC_READ_TIME_OUT]
            if (readTimeOut != null) {
                check(readTimeOut)
                val timeUnit =
                    DynamicHeard.convertTimeUnit(headers[DynamicHeard.DYNAMIC_READ_TIME_OUT_TimeUnit])
                RxHttpLogTool.i("修改 read time out $readTimeOut")
                newChain =
                    newChain.withReadTimeout(readTimeOut.toInt(), timeUnit)
            }


            val writeTimeOut = headers[DynamicHeard.DYNAMIC_WRITE_TIME_OUT]
            if (writeTimeOut != null) {
                check(writeTimeOut)
                val timeUnit =
                    DynamicHeard.convertTimeUnit(headers[DynamicHeard.DYNAMIC_WRITE_TIME_OUT_TimeUnit])
                RxHttpLogTool.i("修改 write time out $writeTimeOut")
                newChain =
                    newChain.withWriteTimeout(writeTimeOut.toInt(), timeUnit)
            }


            val connectTimeOut = headers[DynamicHeard.DYNAMIC_CONNECT_TIME_OUT]
            if (connectTimeOut != null) {
                check(connectTimeOut)
                val timeUnit =
                    DynamicHeard.convertTimeUnit(headers[DynamicHeard.DYNAMIC_CONNECT_TIME_OUT_TimeUnit])
                RxHttpLogTool.i("修改 connect time out $connectTimeOut")
                newChain =
                    newChain.withConnectTimeout(connectTimeOut.toInt(), timeUnit)
            }
        }
        val newBuilder = request.newBuilder()
        newBuilder.removeHeader(DynamicHeard.DYNAMIC_READ_TIME_OUT)
        newBuilder.removeHeader(DynamicHeard.DYNAMIC_READ_TIME_OUT_TimeUnit)
        newBuilder.removeHeader(DynamicHeard.DYNAMIC_CONNECT_TIME_OUT)
        newBuilder.removeHeader(DynamicHeard.DYNAMIC_CONNECT_TIME_OUT_TimeUnit)
        newBuilder.removeHeader(DynamicHeard.DYNAMIC_WRITE_TIME_OUT)
        newBuilder.removeHeader(DynamicHeard.DYNAMIC_WRITE_TIME_OUT_TimeUnit)
        return newChain.proceed(newBuilder.build())
    }

    private fun check(timeout: String) {
        if (timeout.toLong() < 0) {
            throw Throwable("time out must > 0 ,please check DynamicTimeout")
        }
    }

}