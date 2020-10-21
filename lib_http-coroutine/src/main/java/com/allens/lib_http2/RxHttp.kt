package com.allens.lib_http2

import android.content.Context
import com.allens.lib_http2.config.*
import com.allens.lib_http2.download.utils.FileTool
import com.allens.lib_http2.impl.*
import com.allens.lib_http2.interceptor.OnCookieListener
import com.allens.lib_http2.manager.HttpManager
import com.allens.lib_http2.tools.RequestBuilder
import retrofit2.CallAdapter
import retrofit2.Converter
import kotlin.collections.HashMap


/**
 *
 * @Description:
 * @Author:         Allens
 * @CreateDate:     2019-11-22 11:48
 * @Version:        1.0
 */
class RxHttp {


    companion object {
        const val TAG = "RxHttp"
    }


    class Builder {
        /***
         * 配置通用的 超时时间
         * [time] 单位秒
         */
        fun writeTimeout(time: Long): Builder {
            HttpConfig.writeTime = time
            return this
        }

        /***
         * 配置通用的 超时时间
         * [time] 单位秒
         */
        fun readTimeout(time: Long): Builder {
            HttpConfig.readTime = time
            return this
        }

        /***
         * 配置通用的 超时时间
         * [time] 单位秒
         */
        fun connectTimeout(time: Long): Builder {
            HttpConfig.connectTime = time
            return this
        }

        fun retryOnConnectionFailure(retryOnConnectionFailure: Boolean): Builder {
            HttpConfig.retryOnConnectionFailure = retryOnConnectionFailure
            return this
        }

        /***
         * 是否显示log
         * 1.
         * 使用 [addLogInterceptorListener] 方法
         * 可以获取到 日志拦截器[com.allens.lib_http2.interceptor.LogInterceptor]中获取的数据
         *
         * 2. 在[com.allens.lib_http2.tools.RxHttpLogTool] 中 可以查看一些记录的日志，方便调试
         * [isLog] true 显示log
         */
        fun isLog(isLog: Boolean): Builder {
            HttpConfig.isLog = isLog
            return this
        }

        fun level(level: HttpLevel): Builder {
            HttpConfig.level = level
            return this
        }


        /***
         * 返回日志的信息
         */
        fun addLogInterceptorListener(logListener: OnLogInterceptorListener): Builder {
            HttpConfig.logListener = logListener
            return this
        }


        /**
         * retrofit2 中的工厂方法，能够为其添加额外的 [Converter.Factory] 或者 [CallAdapter.Factory]
         */
        fun addFactoryListener(listener: OnFactoryListener): Builder {
            HttpConfig.onFactoryListener = listener
            return this
        }

        /***
         * 为所有请求都添加heard
         */
        fun addHead(listener: OnHeardListener): Builder {
            val hashMap = HashMap<String, String>()
            listener.onHeardMap(hashMap)
            HttpConfig.heardMap = hashMap
            return this
        }

        fun addCookieInterceptor(
            cookieListener: OnCookieListener,
            onCookieInterceptor: OnCookieInterceptor
        ): Builder {
            HttpConfig.cookieListener = cookieListener
            HttpConfig.onCookieInterceptor = onCookieInterceptor
            return this
        }

        /***
         * 设置网络的请求的url,如果需要对某一个请求单独配置其他的url ,
         * 请使用 [com.allens.lib_http2.tools.RequestBuilder.dynamicBaseUrl]，
         * 为某一个请求单独配置
         */
        fun baseUrl(url: String): Builder {
            HttpConfig.baseUrl = url
            return this
        }


        /***
         *
         * 有网时:[time]秒之后请求数据  默认20秒
         * 缓存策略 选择
         * [CacheType.HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_NO_TIME]
         * [CacheType.HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_HAS_TIME]
         * 时候生效
         *
         * 实际上只是在请求的时候带上请求头 [max-age=time] max-age:最大缓存时间
         */
        fun cacheNetWorkTimeOut(time: Int): Builder {
            HttpConfig.cacheNetworkTimeOut = time
            return this
        }

        /***
         * 无网时 特定时间之前  会将 有网时候请求到的数据 返回
         *
         * 默认是30天。
         * [time] 单位 秒
         */
        fun cacheNoNetWorkTimeOut(time: Int): Builder {
            HttpConfig.cacheNoNetworkTimeOut = time
            return this
        }

        /***
         * 缓存的大小，默认 10M
         * [size] 单位 byte
         */
        fun cacheSize(size: Int): Builder {
            HttpConfig.cacheSize = size
            return this
        }

        /**
         * 网络缓存的位置
         * 默认位置 沙盒位置/cacheHttp
         */
        fun cachePath(path: String): Builder {
            HttpConfig.cachePath = path
            return this
        }

        /***
         * 缓存策略 默认无缓存策略
         */
        fun cacheType(type: CacheType): Builder {
            when (type) {
                CacheType.HAS_NETWORK_NOCACHE_AND_NO_NETWORK_NO_TIME -> {
                    HttpConfig.cacheNetWorkType = HttpNetWorkType.NOCACHE
                    HttpConfig.cacheNoNewWorkType = HttpCacheType.NO_TIMEOUT
                }
                CacheType.HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_NO_TIME -> {
                    HttpConfig.cacheNetWorkType = HttpNetWorkType.CACHE_TIME
                    HttpConfig.cacheNoNewWorkType = HttpCacheType.NO_TIMEOUT
                }
                CacheType.HAS_NETWORK_NOCACHE_AND_NO_NETWORK_HAS_TIME -> {
                    HttpConfig.cacheNetWorkType = HttpNetWorkType.NOCACHE
                    HttpConfig.cacheNoNewWorkType = HttpCacheType.HAS_TIMEOUT
                }
                CacheType.HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_HAS_TIME -> {
                    HttpConfig.cacheNetWorkType = HttpNetWorkType.CACHE_TIME
                    HttpConfig.cacheNoNewWorkType = HttpCacheType.HAS_TIMEOUT
                }
                CacheType.NONE -> {
                    HttpConfig.cacheNetWorkType = HttpNetWorkType.NONE
                    HttpConfig.cacheNoNewWorkType = HttpCacheType.NONE
                }
            }
            return this
        }

        fun build(context: Context): RxHttp {
            HttpManager.create().build(context)
            return RxHttp()
        }
    }


    //创建一个新的请求
    fun create(): RequestBuilder {
        return RequestBuilder()
    }
}

