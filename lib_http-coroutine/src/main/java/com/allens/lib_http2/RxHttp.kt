package com.allens.lib_http2

import android.content.Context
import com.allens.lib_http2.config.*
import com.allens.lib_http2.core.HttpResult
import com.allens.lib_http2.download.DownLoadManager
import com.allens.lib_http2.download.utils.FileTool
import com.allens.lib_http2.impl.*
import com.allens.lib_http2.interceptor.OnCookieListener
import com.allens.lib_http2.manager.HttpManager
import com.allens.lib_http2.tools.RequestBuilder
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
        fun writeTimeout(time: Long): Builder {
            HttpConfig.writeTime = time
            return this
        }

        fun readTimeout(time: Long): Builder {
            HttpConfig.readTime = time
            return this
        }

        fun connectTimeout(time: Long): Builder {
            HttpConfig.connectTime = time
            return this
        }

        fun retryOnConnectionFailure(retryOnConnectionFailure: Boolean): Builder {
            HttpConfig.retryOnConnectionFailure = retryOnConnectionFailure
            return this
        }

        fun isLog(isLog: Boolean): Builder {
            HttpConfig.isLog = isLog
            return this
        }

        fun level(level: HttpLevel): Builder {
            HttpConfig.level = level
            return this
        }

        fun addLogFilter(listener: OnLogFilterListener): Builder {
            HttpConfig.logFilterListener = listener
            return this
        }

        fun addLogListener(logListener: OnLogListener): Builder {
            HttpConfig.logListener = logListener
            return this
        }

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

        fun baseUrl(url: String): Builder {
            HttpConfig.baseUrl = url
            return this
        }


        fun cacheNetWorkTimeOut(time: Int): Builder {
            HttpConfig.cacheNetworkTimeOut = time
            return this
        }

        fun cacheNoNetWorkTimeOut(time: Int): Builder {
            HttpConfig.cacheNoNetworkTimeOut = time
            return this
        }

        fun cacheSize(size: Int): Builder {
            HttpConfig.cacheSize = size
            return this
        }

        fun cachePath(path: String): Builder {
            HttpConfig.cachePath = path
            return this
        }

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


    //==============================================================================================
    // 请求方法
    //==============================================================================================


    fun create(): RequestBuilder {
        return RequestBuilder()
    }


    inline fun <T : Any> checkResult(
        result: HttpResult<T>,
        success: (T) -> Unit,
        error: (String?) -> Unit
    ) {
        if (result is HttpResult.Success) {
            success(result.data)
        } else if (result is HttpResult.Error) {
            error(result.throwable.message)
        }
    }

    //格式化小数
    fun bytes2kb(bytes: Long): String {
        return FileTool.bytes2kb(bytes)
    }
}

