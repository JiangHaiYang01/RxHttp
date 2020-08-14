package com.allens.lib_http2.interceptor

import com.allens.lib_http2.tools.RequestBuilder
import okhttp3.Interceptor
import okhttp3.Response

class HostSelectionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        var headers = request.headers
        return if (headers.size > 0) {
            for (info in headers) {
                if (info.first == RequestBuilder.CHANGE_URL) {
                    val httpUrl = request.url
                    val newBuilder = httpUrl.newBuilder()
                    newBuilder.host(info.second)
                    request = request.newBuilder()
                        .removeHeader(RequestBuilder.CHANGE_URL)
                        .url(newBuilder.build())
                        .build()
                    return chain.proceed(request)
                }
            }
            chain.proceed(request)
        } else {
            chain.proceed(request)
        }
    }

}