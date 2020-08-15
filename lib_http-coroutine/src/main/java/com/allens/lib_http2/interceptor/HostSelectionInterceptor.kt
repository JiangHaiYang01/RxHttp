package com.allens.lib_http2.interceptor

import android.util.Log
import com.allens.lib_http2.tools.RequestBuilder
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URL

class HostSelectionInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        var headers = request.headers
        return if (headers.size > 0) {
            for (info in headers) {
                if (info.first == RequestBuilder.CHANGE_URL) {
                    val httpUrl = request.url
                    val newBuilder = httpUrl.newBuilder()

                    when {
                        info.second.startsWith("https") -> {
                            newBuilder.scheme("https")
                        }
                        info.second.startsWith("http") -> {
                            newBuilder.scheme("http")
                        }
                        else -> {
                            throw Throwable("${info.second} is not https or http , please check this base url")
                        }
                    }

                    try {
                        val url = URL(info.second)
                        val domain: String = url.host
                        newBuilder.host(domain)
                    } catch (throwable: Throwable) {
                        throw  Throwable("${info.second} is invalid base url ,please check this base url")
                    }


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