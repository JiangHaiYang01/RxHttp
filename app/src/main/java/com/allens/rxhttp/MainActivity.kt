package com.allens.rxhttp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpLevel
import com.allens.lib_http2.impl.OnFactoryListener
import com.allens.lib_http2.impl.OnLogInterceptorListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope(),
    OnLogInterceptorListener {

    companion object {
        const val TAG = "Main"
    }

    private lateinit var rxHttp: RxHttp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        log.movementMethod = ScrollingMovementMethod.getInstance()

        //初始化
        rxHttp = RxHttp.Builder()
            .baseUrl("https://www.wanandroid.com") //base url
            .isLog(true)  //是否打印log
            .level(HttpLevel.BODY)      //日志级别
            .addLogInterceptorListener(this) //日志拦截器的日志信息
            .writeTimeout(10)  //超时
            .readTimeout(10)
            .connectTimeout(10)
            .retryOnConnectionFailure(true)//是否重试
            .addFactoryListener(object : OnFactoryListener {
                //添加自定义的 Converter.Factory
                override fun addConverterFactory(): MutableSet<Converter.Factory> {
                    return mutableSetOf(
                        GsonConverterFactory.create()
                    )
                }

                override fun addCallAdapterFactory(): MutableSet<CallAdapter.Factory>? {//添加自定义的 CallAdapter.Factory
                    return mutableSetOf(
                        RxJava2CallAdapterFactory.create()
                    )
                }
            })
            .build(this)

        btn_get.setOnClickListener {
            log.text = ""
            getRequest()
        }
        btn_form.setOnClickListener {
            log.text = ""
            formRequest()
        }
        btn_post.setOnClickListener {
            log.text = ""
            postRequest()
        }


        btn_download.setOnClickListener {
            startActivity(Intent(this, DownLoadAct::class.java))
        }
        btn_upload.setOnClickListener {
            startActivity(Intent(this, UploadAct::class.java))
        }

        btn_change_base_url.setOnClickListener {
            log.text = ""
            changeUrlRequest()
        }

        btn_change_time_out.setOnClickListener {
            log.text = ""
            changeTimeOutRequest()
        }
    }

    private fun formRequest() {
        launch {
            rxHttp
                .create()
                .addParameter("title", "123456")
                .addParameter("author", "123456")
                .addParameter("link", "123456")
                .doPost("lg/collect/add/json", TestBean::class.java)
                .result(
                    {
//                        log.text = it.toString()
                    },
                    {
//                        log.text = it.message.toString()
                    }
                )
        }
    }

    private fun postRequest() {
        launch {
            rxHttp
                .create()
                .addParameter("title", "123456")
                .addParameter("author", "123456")
                .addParameter("link", "123456")
                .doBody("lg/collect/add/json", TestBean::class.java)
                .result(
                    {
//                        log.text = it.toString()
                    },
                    {
//                        log.text = it.message.toString()
                    }
                )
        }
    }

    private fun changeUrlRequest() {
        launch {
            rxHttp
                .create()
                .dynamicBaseUrl("http://localhost")
                .addParameter("k", "java")
                .doGet(parameter = "wxarticle/chapters/json", tClass = TestBean::class.java)
                .result({
                    Log.i(TAG, "success ${Thread.currentThread().name} info $it ")
//                    log.text = it.toString()
                }, {
                    Log.i(TAG, "error ${Thread.currentThread().name} info ${it.toString()} ")
//                    log.text = it.toString()
                })
        }
    }

    private fun changeTimeOutRequest() {
        launch {
            rxHttp
                .create()
                .dynamicConnectTimeOut(10)
                .dynamicReadTimeOut(10)
                .dynamicWriteTimeOut(10)
                .addParameter("title", "123456")
                .addParameter("author", "123456")
                .addParameter("link", "123456")
                .doBody("lg/collect/add/json", TestBean::class.java)
                .result(
                    {
//                        log.text = it.toString()
                    },
                    {
//                        log.text = it.message.toString()
                    }
                )
        }
    }


    private fun getRequest() {
        launch {
            rxHttp
                .create()
                .addParameter("k", "java")
                .doGet(parameter = "wxarticle/chapters/json", tClass = TestBean::class.java)
                .result({
                    Log.i(TAG, "success ${Thread.currentThread().name} info $it ")
//                    log.text = it.toString()
                }, {
                    Log.i(TAG, "error ${Thread.currentThread().name} info ${it.toString()} ")
//                    log.text = it.toString()
                })
        }
    }

    override fun onLogInterceptorInfo(message: String) {
        log.append(message + "\n")
    }
}
