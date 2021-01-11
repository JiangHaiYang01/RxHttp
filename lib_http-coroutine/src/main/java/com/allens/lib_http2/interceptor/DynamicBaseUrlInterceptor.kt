package com.allens.lib_http2.interceptor

import com.allens.lib_http2.tools.DynamicHeard
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
            val url = headers[DynamicHeard.DYNAMIC_URL]
            if (url != null) {
                val httpUrl = request.url
                val newBuilder = httpUrl.newBuilder()
                checkBaseUrl(url, newBuilder)
                setNewHost(url, newBuilder)
                request = request.newBuilder()
                    .removeHeader(DynamicHeard.DYNAMIC_URL)
                    .url(newBuilder.build())
                    .build()
                return chain.proceed(request)
            }
        }
        return chain.proceed(request)
    }

    private fun setNewHost(
        newUrl: String,
        newBuilder: HttpUrl.Builder
    ) {
        try {
            val url = URL(newUrl)
            val domain: String = url.host
            newBuilder.host(domain)
        } catch (throwable: Throwable) {
            throw  Throwable("$newUrl is invalid base url ,please check this base url")
        }
    }

    private fun checkBaseUrl(
        newUrl: String,
        newBuilder: HttpUrl.Builder
    ) {
        when {
            newUrl.startsWith("https") -> {
                newBuilder.scheme("https")
            }
            newUrl.startsWith("http") -> {
                newBuilder.scheme("http")
            }
            else -> {
                throw Throwable("$newUrl is not https or http , please check this base url")
            }
        }
    }

}