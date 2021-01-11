package com.allens.lib_http2.manager

import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Looper
import com.allens.lib_http2.config.HttpConfig
import com.allens.lib_http2.config.HttpNetWorkType
import com.allens.lib_http2.interceptor.*
import com.allens.lib_http2.interceptor.CacheInterceptor
import com.allens.lib_http2.interceptor.CacheNetworkInterceptor
import com.allens.lib_http2.tools.RxHttpLogTool
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.Gson
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File


/**
 *
 * @Description:
 * @Author:         Allens
 * @CreateDate:     2019-11-22 11:52
 * @Version:        1.0
 */
object HttpManager {


    val handler = Handler(Looper.getMainLooper())
    lateinit var context: Context
    lateinit var httpConfig: HttpConfig

    fun create(httpConfig: HttpConfig): HttpManager {
        this.httpConfig = httpConfig
        createBuilder()
        return this
    }

    fun build(context: Context): HttpManager {
        this.context = context
        buildOkHttp(context)
        retrofit = createRetrofit(httpConfig.baseUrl)
        retrofitDownLoad = createRetrofitByDownLoad()
        return this
    }


    private lateinit var okHttpBuilder: OkHttpClient.Builder
    private lateinit var retrofitBuilder: Retrofit.Builder
    lateinit var retrofit: Retrofit
    private lateinit var retrofitDownLoad: Retrofit
    val gson: Gson = Gson()

    private fun createBuilder() {
        okHttpBuilder = OkHttpClient.Builder()
        retrofitBuilder = Retrofit.Builder()
    }

    private fun buildOkHttp(context: Context) {

        val cookieJar =
            PersistentCookieJar(
                SetCookieCache(),
                SharedPrefsCookiePersistor(context.applicationContext)
            )
        //第三方库 管理 cookie
        okHttpBuilder.cookieJar(cookieJar)
        okHttpBuilder.connectTimeout(httpConfig.connectTime, httpConfig.connectTimeTimeUnit)
        okHttpBuilder.readTimeout(httpConfig.readTime, httpConfig.readTimeTimeUnit)
        okHttpBuilder.writeTimeout(httpConfig.writeTime, httpConfig.writeTimeTimeUnit)
        okHttpBuilder.retryOnConnectionFailure(httpConfig.retryOnConnectionFailure)


        //动态替换BaseURL
        okHttpBuilder.addInterceptor(DynamicBaseUrlInterceptor)
        //动态替换连接超时
        okHttpBuilder.addInterceptor(DynamicTimeoutInterceptor)

        //添加日志拦截器
        okHttpBuilder.addInterceptor(LogInterceptor.register(httpConfig))
        val map = httpConfig.heardMap

        //添加请求头
        if (!map.isNullOrEmpty()) {
            okHttpBuilder.addInterceptor(HeardInterceptor.register(map))
        }

        //cookie 拦截器
        httpConfig.cookieSet.forEach {
            okHttpBuilder.addInterceptor(ReceivedCookieInterceptor.register(it))
        }

        //cache 缓存
        val cacheSize = httpConfig.cacheSize // 10 MiB
        val cache = Cache(
            File(
                if (httpConfig.cachePath.isEmpty()) {
                    getBasePath(context) + "/cacheHttp"
                } else {
                    httpConfig.cachePath
                }
            ), cacheSize.toLong()
        )
        //设置缓存
        if (httpConfig.cacheNetWorkType != HttpNetWorkType.NONE) {
            okHttpBuilder
                .addInterceptor(CacheInterceptor(context, httpConfig))
                .addNetworkInterceptor(CacheNetworkInterceptor(httpConfig))
                .cache(cache)
        }

    }

    private fun createRetrofit(baseUrl: String): Retrofit {
        val client = retrofitBuilder
            .client(okHttpBuilder.build())

        httpConfig.callAdapterFactorySet.forEach {
            try {
                client.addCallAdapterFactory(it)
                RxHttpLogTool.i("addCallAdapterFactory $it")
            } catch (throwable: Throwable) {
                RxHttpLogTool.i("add factory failed ${throwable.message}")
            }
        }


        httpConfig.converterFactorySet.forEach {
            try {
                client.addConverterFactory(it)
                RxHttpLogTool.i("addConverterFactory $it")
            } catch (throwable: Throwable) {
                RxHttpLogTool.i("add converterFactory failed ${throwable.message}")
            }
        }
        return client
            .baseUrl(baseUrl)
            .build()
    }

    /***
     * 下载和网络请求使用不同的retrofit
     * 防止@Steaming 不起作用
     * @return Retrofit
     */
    private fun createRetrofitByDownLoad(): Retrofit {
        okHttpBuilder.interceptors().clear()
        return createRetrofit(httpConfig.baseUrl)
    }


    fun <T> getService(tClass: Class<T>): T {
        return retrofit.create(tClass)
    }

    fun <T> getServiceFromDownLoadOrUpload(tClass: Class<T>): T {
        return retrofitDownLoad.create(tClass)
    }


    //获取更路径
    fun getBasePath(context: Context): String {
        var p: String = Environment.getExternalStorageState()
        val f: File? = context.getExternalFilesDir(null)
        if (null != f) {
            p = f.absolutePath
        }
        return p
    }

}