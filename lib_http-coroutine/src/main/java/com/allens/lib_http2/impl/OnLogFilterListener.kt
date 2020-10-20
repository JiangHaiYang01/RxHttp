package com.allens.lib_http2.impl

/**
 *
 * @Description:
 * @Author:         Allens
 * @CreateDate:     2019-11-22 16:24
 * @Version:        1.0
 */
interface OnLogListener {
    interface OnLogFilterListener {
        fun logFilter(message: String): Boolean
    }

    interface OnLogInterceptorListener {
        fun onLogInterceptorInfo(message: String);
    }

}