package com.allens.lib_http2.impl

/**
 *
 * @Description:
 * @Author:         Allens
 * @CreateDate:     2019-11-22 16:24
 * @Version:        1.0
 */
interface OnLogFilterListener {
    fun filter(message: String): Boolean
}


interface OnLogListener{
    fun onRxHttpLog(message: String);
}
