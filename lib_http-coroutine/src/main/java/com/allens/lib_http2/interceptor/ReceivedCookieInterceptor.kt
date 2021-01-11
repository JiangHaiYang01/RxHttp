package com.allens.lib_http2.interceptor

import com.allens.lib_http2.impl.OnCookieInterceptor
import okhttp3.Interceptor
import okhttp3.Response

//cookie 拦截器
object ReceivedCookieInterceptor {
    fun register(interceptor: OnCookieInterceptor): Interceptor {
        return ReceivedCookiesInterceptorImpl(interceptor)
    }
}


class ReceivedCookiesInterceptorImpl(
    private val interceptor: OnCookieInterceptor
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        //不是拦截全部
        if (!interceptor.isInterceptorAllRequest()) {
            val url = response.request.url.toUrl().toString()
            if (interceptor.isInterceptorRequest(url)) {
                interceptor(response)
            }
            return response
        }

        //这里获取请求返回的cookie
        interceptor(response)

        return response
    }

    private fun interceptor(response: Response) {
        if (response.headers("Set-Cookie").isNotEmpty()) {
            val cookies = HashSet<String>()
            for (header in response.headers("Set-Cookie")) {
                cookies.add(header)
            }
            interceptor.onCookies(cookies)
        }
    }
}



