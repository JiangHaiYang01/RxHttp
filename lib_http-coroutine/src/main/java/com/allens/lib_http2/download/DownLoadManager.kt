package com.allens.lib_http2.download

import android.os.Looper
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpConfig
import com.allens.lib_http2.download.utils.DownLoadPool
import com.allens.lib_http2.download.utils.FileTool
import com.allens.lib_http2.download.utils.ShareDownLoadUtil
import com.allens.lib_http2.impl.ApiService
import com.allens.lib_http2.impl.OnDownLoadListener
import com.allens.lib_http2.manager.HttpManager
import com.allens.lib_http2.tools.RxHttpLogTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import java.io.File

object DownLoadManager {

    private const val TAG = RxHttp.TAG

    suspend fun downLoad(
        tag: String,
        url: String,
        savePath: String,
        saveName: String,
        loadListener: OnDownLoadListener
    ) {
        withContext(Dispatchers.IO) {
            doDownLoad(tag, url, savePath, saveName, loadListener, this)
        }
    }


    fun cancel(key: String) {
        val listener = DownLoadPool.getListenerFromKey(key)
        listener?.onDownLoadCancel(key)
        val path = DownLoadPool.getPathFromKey(key)
        if (path != null) {
            val file = File(path)
            if (file.exists()) {
                file.delete()
            }
        }
        DownLoadPool.remove(key)
    }


    fun pause(key: String) {
        val listener = DownLoadPool.getListenerFromKey(key)
        listener?.onDownLoadPause(key)
        DownLoadPool.pause(key)

    }

    fun doDownLoadCancelAll() {
        DownLoadPool.getListenerMap().forEach {
            cancel(it.key)
        }
    }

    fun doDownLoadPauseAll() {
        DownLoadPool.getListenerMap().forEach {
            pause(it.key)
        }
    }

    private suspend fun doDownLoad(
        tag: String,
        url: String,
        savePath: String,
        saveName: String,
        loadListener: OnDownLoadListener,
        coroutineScope: CoroutineScope
    ) {
        //判断是否已经在队列中
        val scope = DownLoadPool.getScopeFromKey(tag)
        if (scope != null && scope.isActive) {
            if (HttpConfig.isLog)
                RxHttpLogTool.i(TAG, "key $tag 已经在队列中")
            return
        } else if (scope != null && !scope.isActive) {
            if (HttpConfig.isLog)
                RxHttpLogTool.i(TAG, "key $tag 已经在队列中 但是已经不再活跃 remove")
            DownLoadPool.removeExitSp(tag)
        }

        if (HttpConfig.isLog) {
            RxHttpLogTool.i(TAG, "startDownLoad key: $tag  url:$url  savePath: $savePath  saveName:$saveName")
        }

        if (saveName.isEmpty()) {
            withContext(Dispatchers.Main) {
                loadListener.onDownLoadError(tag, Throwable("save name is Empty"))
            }
            return
        }

        if (Looper.getMainLooper().thread == Thread.currentThread()) {
            withContext(Dispatchers.Main) {
                loadListener.onDownLoadError(tag, Throwable("current thread is in main thread"))
            }
            return
        }

        val file = File("$savePath/$saveName")
        val currentLength = if (!file.exists()) {
            0L
        } else {
            ShareDownLoadUtil.getLong(tag, 0)
        }
        if (HttpConfig.isLog) {
            RxHttpLogTool.i(TAG, "startDownLoad current $currentLength")
        }

        try {
            if (HttpConfig.isLog) {
                RxHttpLogTool.i(TAG, "add pool")
            }
            //添加到pool
            DownLoadPool.add(tag, coroutineScope)
            DownLoadPool.add(tag, "$savePath/$saveName")
            DownLoadPool.add(tag, loadListener)

            withContext(Dispatchers.Main) {
                loadListener.onDownLoadPrepare(key = tag)
            }

            val response = HttpManager.getServiceFromDownLoadOrUpload(ApiService::class.java)
                .downloadFile("bytes=$currentLength-", url)
            val responseBody = response.body()
            if (responseBody == null) {
                if (HttpConfig.isLog) {
                    RxHttpLogTool.i(TAG, "responseBody is null")
                    withContext(Dispatchers.Main) {
                        loadListener.onDownLoadError(
                            key = tag,
                            throwable = Throwable("responseBody is null please check download url")
                        )
                    }
                    DownLoadPool.remove(tag)
                }
                return
            }


            FileTool.downToFile(
                tag,
                savePath,
                saveName,
                currentLength,
                responseBody,
                loadListener
            )
        } catch (throwable: Throwable) {
            withContext(Dispatchers.Main) {
                loadListener.onDownLoadError(key = tag, throwable = throwable)
            }
            DownLoadPool.remove(tag)
        }
    }
}


