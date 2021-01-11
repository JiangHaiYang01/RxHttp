package com.allens.lib_http2.impl

interface  OnCookieInterceptor {
    //是否拦截所有方法的cookie
    fun isInterceptorAllRequest(): Boolean

    //拦截哪一个方法
    fun isInterceptorRequest(url: String): Boolean

    //拦截返回
    fun onCookies(cookie: HashSet<String>)
}