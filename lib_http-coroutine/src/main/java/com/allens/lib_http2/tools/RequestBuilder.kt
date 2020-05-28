package com.allens.lib_http2.tools

import android.os.Handler
import com.allens.lib_http2.core.HttpResult
import com.allens.lib_http2.download.DownLoadManager
import com.allens.lib_http2.impl.ApiService
import com.allens.lib_http2.impl.OnDownLoadListener
import com.allens.lib_http2.impl.OnUpLoadListener
import com.allens.lib_http2.manager.HttpManager
import com.allens.lib_http2.upload.ProgressRequestBody
import com.allens.lib_http2.upload.UpLoadPool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*


class RequestBuilder {

    private val heard = HashMap<String, String>()
    private val map = HashMap<String, Any>()
    private val bodyMap = HashMap<String, ProgressRequestBody>()


    private var handler: Handler? = null
    fun addHeard(key: String, value: String): RequestBuilder {
        heard[key] = value
        return this
    }

    fun addParameter(key: String, value: Any): RequestBuilder {
        map[key] = value
        return this
    }


    fun addFile(key: String, file: File): RequestBuilder {
        handler = Handler()
        val fileBody: RequestBody =
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        bodyMap[key] = ProgressRequestBody(null, "", fileBody, handler)
        return this
    }


    suspend fun <T : Any> doGet(
        parameter: String,
        tClass: Class<T>
    ): HttpResult<T> {
        return executeResponse(
            {
                val baseUrl = HttpManager.retrofit.baseUrl().toUrl().toString()
                var getUrl: String = baseUrl + parameter
                if (map.size > 0) {
                    val param: String = UrlTool.prepareParam(map)
                    if (param.trim().isNotEmpty()) {
                        getUrl += "?$param"
                    }
                }
                HttpManager.getService(ApiService::class.java)
                    .doGet(heard, getUrl)
                    .body()
                    ?.string()
            }, tClass
        )
    }

    suspend fun <T : Any> doPost(parameter: String, tClass: Class<T>): HttpResult<T> {
        return executeResponse({
            HttpManager.getService(ApiService::class.java)
                .doPost(parameter, heard, map).body()
                ?.string()
        }, tClass)
    }


    suspend fun <T : Any> doBody(parameter: String, tClass: Class<T>): HttpResult<T> {
        return executeResponse({
            val toJson = HttpManager.gson.toJson(map)
            val requestBody =
                toJson.toRequestBody("application/json".toMediaTypeOrNull())
            HttpManager.getService(ApiService::class.java)
                .doBody(parameter, heard, requestBody)
                .body()
                ?.string()
        }, tClass)
    }


    suspend fun <T : Any> doDelete(parameter: String, tClass: Class<T>): HttpResult<T> {
        return executeResponse({
            HttpManager.getService(ApiService::class.java)
                .doDelete(parameter, heard, map).body()
                ?.string()
        }, tClass)
    }

    suspend fun <T : Any> doPut(parameter: String, tClass: Class<T>): HttpResult<T> {
        return executeResponse({
            HttpManager.getService(ApiService::class.java)
                .doPut(parameter, heard, map).body()
                ?.string()
        }, tClass)
    }


    private suspend fun <T : Any> executeResponse(
        call: suspend () -> String?,
        tClass: Class<T>
    ): HttpResult<T> {
        return try {
            HttpResult.Success(HttpManager.gson.fromJson(call(), tClass))
        } catch (e: Throwable) {
            HttpResult.Error(e)
        }
    }


    //下载
    suspend fun doDownLoad(
        tag: String,
        url: String,
        savePath: String,
        saveName: String,
        loadListener: OnDownLoadListener
    ) {
        DownLoadManager.downLoad(
            tag, url, savePath, saveName, loadListener = loadListener
        )
    }


    //下载 cancel
    fun doDownLoadCancel(key: String) {
        DownLoadManager.cancel(key)
    }

    //暂停下载
    fun doDownLoadPause(key: String) {
        DownLoadManager.pause(key)
    }

    //暂停所有下载
    fun doDownLoadPauseAll() {
        DownLoadManager.doDownLoadPauseAll()
    }

    //取消所有下载
    fun doDownLoadCancelAll() {
        DownLoadManager.doDownLoadCancelAll()
    }


    //上传
    private suspend fun <T : Any> doUpload(url: String, tClass: Class<T>): HttpResult<T> {
        return executeResponse({
            HttpManager.getServiceFromDownLoadOrUpload(ApiService::class.java)
                .upFileList(url, heard, map, bodyMap).body()
                ?.string()
        }, tClass)
    }


    suspend fun <T : Any> doUpload(
        tag: String,
        url: String,
        tClass: Class<T>,
        listener: OnUpLoadListener<T>
    ) {
        withContext(Dispatchers.IO) {
            for ((key, value) in bodyMap) {
                bodyMap[key] = ProgressRequestBody(listener, tag, value.getRequestBody(), handler)
            }
            UpLoadPool.add(tag, listener, this)
            withContext(Dispatchers.Main) {
                listener.opUploadPrepare(tag)
            }
            val doUpload = doUpload(url, tClass)
            checkResult(doUpload, {
                withContext(Dispatchers.Main) {
                    listener.onUpLoadSuccess(tag, it)
                }

                UpLoadPool.remove(tag)
            }, {
                withContext(Dispatchers.Main) {
                    listener.onUpLoadFailed(tag, throwable = it)
                }
                UpLoadPool.remove(tag)
            })
        }
    }

    fun doUpLoadCancel(tag: String) {
        UpLoadPool.getListener(tag)?.onUploadCancel(tag)
        UpLoadPool.remove(tag)
    }


    private inline fun <T : Any> checkResult(
        result: HttpResult<T>,
        success: (T) -> Unit,
        error: (Throwable) -> Unit
    ) {
        if (result is HttpResult.Success) {
            success(result.data)
        } else if (result is HttpResult.Error) {
            error(result.throwable)
        }
    }

}