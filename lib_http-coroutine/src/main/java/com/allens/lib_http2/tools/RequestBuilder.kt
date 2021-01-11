package com.allens.lib_http2.tools

import android.os.Build
import android.os.Looper
import android.util.Log
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
import java.util.concurrent.TimeUnit


class RequestBuilder {

    private val heard = HashMap<String, String>()
    private val map = HashMap<String, Any>()
    private val bodyMap = HashMap<String, ProgressRequestBody>()




    //添加请求头
    fun addHeard(key: String, value: String): RequestBuilder {
        heard[key] = value
        return this
    }

    //添加请求参数
    fun addParameter(key: String, value: Any): RequestBuilder {
        map[key] = value
        return this
    }

    /***
     * 动态切换请求的地址
     * 这里的heard 会在 [com.allens.lib_http2.interceptor.DynamicBaseUrlInterceptor] 中进行判断 然后删除
     */
    fun dynamicBaseUrl(url: String): RequestBuilder {
        addHeard(DynamicHeard.DYNAMIC_URL, url)
        return this
    }

    /***
     * 动态切换connect time
     * 会在 [com.allens.lib_http2.interceptor.DynamicTimeoutInterceptor] 中进行判断然后删除
     */
    fun dynamicConnectTimeOut(timeout: Int,timeUnit: TimeUnit = TimeUnit.MILLISECONDS): RequestBuilder {
        addHeard(DynamicHeard.DYNAMIC_CONNECT_TIME_OUT, timeout.toString())
        addHeard(DynamicHeard.DYNAMIC_CONNECT_TIME_OUT_TimeUnit, DynamicHeard.timeUnitConvert(timeUnit).info)
        return this
    }

    /***
     * 动态切换write time
     * 会在 [com.allens.lib_http2.interceptor.DynamicTimeoutInterceptor] 中进行判断然后删除
     */
    fun dynamicWriteTimeOut(timeout: Int,timeUnit: TimeUnit = TimeUnit.MILLISECONDS): RequestBuilder {
        addHeard(DynamicHeard.DYNAMIC_WRITE_TIME_OUT, timeout.toString())
        addHeard(DynamicHeard.DYNAMIC_WRITE_TIME_OUT_TimeUnit, DynamicHeard.timeUnitConvert(timeUnit).info)
        return this
    }

    /***
     * 动态切换read time
     * 这里的heard 会在 [com.allens.lib_http2.interceptor.DynamicTimeoutInterceptor] 中进行判断 然后删除
     */
    fun dynamicReadTimeOut(timeout: Int,timeUnit: TimeUnit = TimeUnit.MILLISECONDS): RequestBuilder {
        addHeard(DynamicHeard.DYNAMIC_READ_TIME_OUT, timeout.toString())
        addHeard(DynamicHeard.DYNAMIC_READ_TIME_OUT_TimeUnit, DynamicHeard.timeUnitConvert(timeUnit).info)
        return this
    }

    //添加上传的文件
    fun addFile(key: String, file: File): RequestBuilder {
        val fileBody: RequestBody =
            file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        bodyMap[key] = ProgressRequestBody(null, "", fileBody, HttpManager.handler)
        return this
    }


    /**
     * get 请求
     * [parameter] 请求地址,跟在BaseUrl 后面的
     * [tClass]    需要转成的模型类
     */
    suspend fun <T : Any> doGet(
        parameter: String,
        tClass: Class<T>
    ): HttpResult<T> {
        return executeResponse(
            {
                val baseUrl = HttpManager.retrofit.baseUrl().toString()
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

    /**
     * 表单提交
     * [parameter] 请求地址,跟在BaseUrl 后面的
     * [tClass]    需要转成的模型类
     */
    suspend fun <T : Any> doPost(parameter: String, tClass: Class<T>): HttpResult<T> {
        return executeResponse({
            HttpManager.getService(ApiService::class.java)
                .doPost(parameter, heard, map).body()
                ?.string()
        }, tClass)
    }


    /**
     * post请求,json 的方式请求
     * [parameter] 请求地址,跟在BaseUrl 后面的
     * [tClass]    需要转成的模型类
     */
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


    /**
     * delete 请求
     * [parameter] 请求地址,跟在BaseUrl 后面的
     * [tClass]    需要转成的模型类
     */
    suspend fun <T : Any> doDelete(parameter: String, tClass: Class<T>): HttpResult<T> {
        return executeResponse({
            HttpManager.getService(ApiService::class.java)
                .doDelete(parameter, heard, map).body()
                ?.string()
        }, tClass)
    }

    /**
     * put 请求
     * [parameter] 请求地址,跟在BaseUrl 后面的
     * [tClass]    需要转成的模型类
     */
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


    /***
     * 开始下载文件
     * [key]            tag 每一个下载任务需要传入不同的tag,否则会找不到对应的下载task
     * [url]            下载URL
     * [savePath]       保存的位置
     * [saveName]       下载文件保存的名称
     * [loadListener]   下载监听回调
     */
    suspend fun doDownLoad(
        key: String,
        url: String,
        savePath: String,
        saveName: String,
        loadListener: OnDownLoadListener
    ) {
        DownLoadManager.downLoad(
            key, url, savePath, saveName, loadListener = loadListener
        )
    }


    /***
     * 取消下载
     * [key] 启动下载时候的tag
     * 取消下载会在 [OnDownLoadListener] 中触发 [OnDownLoadListener.onDownLoadCancel]
     */
    fun doDownLoadCancel(key: String) {
        DownLoadManager.cancel(key)
    }

    /***
     * 暂停下载
     * [key] 启动下载时候的tag
     * 暂停下载会在 [OnDownLoadListener] 中触发 [OnDownLoadListener.onDownLoadPause]
     */
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


    private suspend fun <T : Any> doUpload(url: String, tClass: Class<T>): HttpResult<T> {
        return executeResponse({
            HttpManager.getServiceFromDownLoadOrUpload(ApiService::class.java)
                .upFileList(url, heard, map, bodyMap).body()
                ?.string()
        }, tClass)
    }

    /***
     * 上传文件
     * [tag]            上传的tag 此任务的唯一标识
     * [url]            上传的地址
     * [tClass]         模型对象
     * [listener]       上传回调
     */
    suspend fun <T : Any> doUpload(
        tag: String,
        url: String,
        tClass: Class<T>,
        listener: OnUpLoadListener<T>
    ) {
        withContext(Dispatchers.IO) {
            for ((key, value) in bodyMap) {
                bodyMap[key] =
                    ProgressRequestBody(listener, tag, value.getRequestBody(), HttpManager.handler)
            }
            UpLoadPool.add(tag, listener, this)
            withContext(Dispatchers.Main) {
                listener.opUploadPrepare(tag)
            }
            doUpload(url, tClass)
                .result(
                    {
                        listener.onUpLoadSuccess(tag, it)
                        UpLoadPool.remove(tag)
                    }, {
                        listener.onUpLoadFailed(tag, throwable = it)
                        UpLoadPool.remove(tag)
                    })
        }
    }

    /***
     * 取消上传
     * [tag]  上传任务的tag
     */
    fun doUpLoadCancel(tag: String) {
        UpLoadPool.getListener(tag)?.onUploadCancel(tag)
        UpLoadPool.remove(tag)
    }

}