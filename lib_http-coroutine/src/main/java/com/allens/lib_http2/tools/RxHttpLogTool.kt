package com.allens.lib_http2.tools

import android.util.Log
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpConfig

object RxHttpLogTool {

    fun i(tag: String, info: String) {
        if (HttpConfig.isLog) {
            Log.i(tag, info)
            HttpConfig.logListener?.onRxHttpLog(info)
        }
    }
}