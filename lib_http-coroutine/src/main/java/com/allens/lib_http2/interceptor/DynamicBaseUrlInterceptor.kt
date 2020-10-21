package com.allens.lib_http2.interceptor

import android.util.Log
import com.allens.lib_http2.tools.RequestBuilder
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URL

// 动态修改baseUrl
object DynamicBaseUrlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        val headers = request.headers

        if (headers.size > 0) {
            headers.forEach {
                when (it.first) {
                    RequestBuilder.DYNAMIC_URL -> {
                        val httpUrl = request.url
                        val newBuilder = httpUrl.newBuilder()
                        checkBaseUrl(it, newBuilder)
                        setNewHost(it, newBuilder)
                        request = request.newBuilder()
                            .removeHeader(RequestBuilder.DYNAMIC_URL)
                            .url(newBuilder.build())
                            .build()
                        return chain.proceed(request)
                    }
                }
            }
        }
        return chain.proceed(request)
    }

    private fun setNewHost(
        it: Pair<String, String>,
        newBuilder: HttpUrl.Builder
    ) {
        try {
            val url = URL(it.second)
            val domain: String = url.host
            newBuilder.host(domain)
        } catch (throwable: Throwable) {
            throw  Throwable("${it.second} is invalid base url ,please check this base url")
        }
    }

    private fun checkBaseUrl(
        it: Pair<String, String>,
        newBuilder: HttpUrl.Builder
    ) {
        when {
            it.second.startsWith("https") -> {
                newBuilder.scheme("https")
            }
            it.second.startsWith("http") -> {
                newBuilder.scheme("http")
            }
            else -> {
                throw Throwable("${it.second} is not https or http , please check this base url")
            }
        }
    }

}