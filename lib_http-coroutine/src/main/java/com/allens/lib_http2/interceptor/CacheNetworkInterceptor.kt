package com.allens.lib_http2.interceptor

import android.util.Log
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpConfig
import com.allens.lib_http2.config.HttpNetWorkType
import okhttp3.Interceptor
import okhttp3.Response

/**
 *
 * @ProjectName:     tools
 * @Package:         com.allens.interceptor
 * @ClassName:       CacheNetworkInterceptor
 * @Description:     类作用描述
 * @Author:          Allens
 * @CreateDate:      2019-12-30 10:03
 * @UpdateUser:      更新者
 * @UpdateDate:      2019-12-30 10:03
 * @UpdateRemark:    更新说明
 * @Version:         1.0
 */
internal class CacheNetworkInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val time = when (HttpConfig.cacheNetWorkType) {
            HttpNetWorkType.NOCACHE -> {
                0
            }
            HttpNetWorkType.CACHE_TIME -> {
                HttpConfig.cacheNetworkTimeOut
            }
            else -> {
                0
            }
        }
        Log.i(
            RxHttp.TAG,
            "http---->  addNetworkInterceptor " + if (time == 0) {
                "每次都请求实时数据"
            } else {
                "$time 秒之后请求数据"
            }
        )
        return chain.proceed(chain.request()).newBuilder()
            //清除头信息 因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
            .removeHeader("Pragma")
            .addHeader("Cache-Control", "max-age=$time")
            .build()
    }

}
