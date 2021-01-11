package com.allens.lib_http2

import android.content.Context
import com.allens.lib_http2.config.*
import com.allens.lib_http2.impl.*
import com.allens.lib_http2.interceptor.OnCookieListener
import com.allens.lib_http2.manager.HttpManager
import com.allens.lib_http2.tools.RequestBuilder
import retrofit2.CallAdapter
import retrofit2.Converter
import java.util.concurrent.TimeUnit
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
        var isDebug = false
    }


    class Builder {


        private val httpConfig = HttpConfig()

        /***
         * 配置通用的 超时时间
         * [time] 单位秒
         */
        fun writeTimeout(time: Long, timeUnit: TimeUnit = TimeUnit.SECONDS): Builder {
            httpConfig.writeTime = time
            httpConfig.writeTimeTimeUnit = timeUnit
            return this
        }

        /***
         * 配置通用的 超时时间
         * [time] 单位秒
         */
        fun readTimeout(time: Long, timeUnit: TimeUnit = TimeUnit.SECONDS): Builder {
            httpConfig.readTime = time
            httpConfig.readTimeTimeUnit = timeUnit
            return this
        }

        /***
         * 配置通用的 超时时间
         * [time] 单位秒
         */
        fun connectTimeout(time: Long, timeUnit: TimeUnit = TimeUnit.SECONDS): Builder {
            httpConfig.connectTime = time
            httpConfig.connectTimeTimeUnit = timeUnit
            return this
        }

        /***
         * 是否重试
         */
        fun retryOnConnectionFailure(retryOnConnectionFailure: Boolean): Builder {
            httpConfig.retryOnConnectionFailure = retryOnConnectionFailure
            return this
        }


        /**
         * debug 模式 显示请求信息
         */
        fun isDebug(isDebug: Boolean): Builder {
            RxHttp.isDebug = isDebug
            return this
        }

        /***
         * 日志级别
         */
        fun level(level: HttpLevel): Builder {
            httpConfig.level = level
            return this
        }


        /***
         * 添加日志组件，会在[OnLogInterceptorListener] 接口返回框架日志信息
         */
        fun addLogInterceptor(logListener: OnLogInterceptorListener): Builder {
            httpConfig.logSet.add(logListener)
            return this
        }


        /***
         * 添加自定义的[Converter.Factory]
         */
        fun addConverterFactory(factory: Converter.Factory): Builder {
            httpConfig.converterFactorySet.add(factory)
            return this
        }

        /***
         * 添加自定义的[CallAdapter.Factory]
         */
        fun addCallAdapterFactory(factory: CallAdapter.Factory): Builder {
            httpConfig.callAdapterFactorySet.add(factory)
            return this
        }


        /***
         * 为所有请求都添加heard
         */
        fun addHead(key:String,value:String): Builder {
            httpConfig.heardMap[key]= value
            return this
        }

        /**
         * 添加 cookie 拦截
         */
        fun addCookieInterceptor(
            onCookieInterceptor: OnCookieInterceptor
        ): Builder {
            httpConfig.cookieSet.add(onCookieInterceptor)
            return this
        }

        /***
         * 设置网络的请求的url,如果需要对某一个请求单独配置其他的url ,
         * 请使用 [com.allens.lib_http2.tools.RequestBuilder.dynamicBaseUrl]，
         * 为某一个请求单独配置
         */
        fun baseUrl(url: String): Builder {
            httpConfig.baseUrl = url
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
            httpConfig.cacheNetworkTimeOut = time
            return this
        }

        /***
         * 无网时 特定时间之前  会将 有网时候请求到的数据 返回
         *
         * 默认是30天。
         * [time] 单位 秒
         */
        fun cacheNoNetWorkTimeOut(time: Int): Builder {
            httpConfig.cacheNoNetworkTimeOut = time
            return this
        }

        /***
         * 缓存的大小，默认 10M
         * [size] 单位 byte
         */
        fun cacheSize(size: Int): Builder {
            httpConfig.cacheSize = size
            return this
        }

        /**
         * 网络缓存的位置
         * 默认位置 沙盒位置/cacheHttp
         */
        fun cachePath(path: String): Builder {
            httpConfig.cachePath = path
            return this
        }

        /***
         * 缓存策略 默认无缓存策略
         */
        fun cacheType(type: CacheType): Builder {
            when (type) {
                CacheType.HAS_NETWORK_NOCACHE_AND_NO_NETWORK_NO_TIME -> {
                    httpConfig.cacheNetWorkType = HttpNetWorkType.NOCACHE
                    httpConfig.cacheNoNewWorkType = HttpCacheType.NO_TIMEOUT
                }
                CacheType.HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_NO_TIME -> {
                    httpConfig.cacheNetWorkType = HttpNetWorkType.CACHE_TIME
                    httpConfig.cacheNoNewWorkType = HttpCacheType.NO_TIMEOUT
                }
                CacheType.HAS_NETWORK_NOCACHE_AND_NO_NETWORK_HAS_TIME -> {
                    httpConfig.cacheNetWorkType = HttpNetWorkType.NOCACHE
                    httpConfig.cacheNoNewWorkType = HttpCacheType.HAS_TIMEOUT
                }
                CacheType.HAS_NETWORK_CACHE_TIME_AND_NO_NETWORK_HAS_TIME -> {
                    httpConfig.cacheNetWorkType = HttpNetWorkType.CACHE_TIME
                    httpConfig.cacheNoNewWorkType = HttpCacheType.HAS_TIMEOUT
                }
                CacheType.NONE -> {
                    httpConfig.cacheNetWorkType = HttpNetWorkType.NONE
                    httpConfig.cacheNoNewWorkType = HttpCacheType.NONE
                }
            }
            return this
        }

        fun build(context: Context): RxHttp {
            HttpManager.create(httpConfig).build(context)
            return RxHttp()
        }
    }


    //创建一个新的请求
    fun create(): RequestBuilder {
        return RequestBuilder()
    }

    /***
     * 传入自己定义的 接口
     * [tClass] class, 提供了默认的 [ApiService],不满足需求可使用这个方法自行定义
     */
    fun <T> getService(tClass: Class<T>): T {
        return HttpManager.getService(tClass)
    }
}

