package com.allens.lib_http2.upload

import com.allens.lib_http2.download.utils.DownLoadPool
import com.allens.lib_http2.impl.OnUpLoadListener
import com.allens.lib_http2.impl.UpLoadCancelListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import java.util.concurrent.ConcurrentHashMap

object UpLoadPool {


    private val scopeMap: ConcurrentHashMap<String, CoroutineScope> = ConcurrentHashMap()
    private val listenerMap: ConcurrentHashMap<String, UpLoadCancelListener> = ConcurrentHashMap()


    fun add(
        key: String,
        listener: UpLoadCancelListener,
        job: CoroutineScope
    ) {
        scopeMap[key] = job
        listenerMap[key] = listener
    }


    fun remove(key: String) {
        val scope = scopeMap[key]
        if (scope != null && scope.isActive) {
            scope.cancel()
        }
        scopeMap.remove(key)
        listenerMap.remove(key)
    }

    fun getListener(tag: String): UpLoadCancelListener? {
        return listenerMap[tag]
    }


}