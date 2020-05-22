package com.allens.lib_http2.interceptor

import android.util.Log
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpConfig
import com.allens.lib_http2.config.HttpLevel
import com.allens.lib_http2.tools.RxHttpLogTool
import okhttp3.logging.HttpLoggingInterceptor

/**
 *
 * @Description:
 * @Author:         Allens
 * @CreateDate:     2019-11-22 14:02
 * @Version:        1.0
 */

//日志拦截器`
object LogInterceptor {
    fun register(level: HttpLevel): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                val logFilterListener = HttpConfig.logFilterListener
                if (logFilterListener != null) {
                    if (logFilterListener.filter(message)) {
                        return
                    }
                }
                RxHttpLogTool.i(RxHttp.TAG, "http----> $message ")
            }
        })
        interceptor.level = HttpLevel.conversion(level)
        return interceptor
    }
}




