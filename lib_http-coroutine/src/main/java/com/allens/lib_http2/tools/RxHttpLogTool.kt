package com.allens.lib_http2.tools

import android.util.Log
import com.allens.lib_http2.config.HttpConfig

object RxHttpLogTool {

    fun i(tag: String, info: String) {
        Log.i(tag, info)
        HttpConfig.logListener?.onRxHttpLog(info)
    }
}