package com.allens.lib_http2.tools

import android.util.Log
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpConfig

object RxHttpLogTool {

    fun i(info: String) {
        if (RxHttp.isDebug) {
            Log.i(RxHttp.TAG, info)
        }

    }
}