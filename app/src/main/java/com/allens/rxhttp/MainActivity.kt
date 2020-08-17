package com.allens.rxhttp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpLevel
import com.allens.lib_http2.impl.OnBuildClientListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    companion object {
        const val TAG = "Main"
    }

    private lateinit var rxHttp: RxHttp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rxHttp = RxHttp.Builder()
            .baseUrl("https://www.wanandroid.com")
            .isLog(true)
            .level(HttpLevel.BODY)
            .writeTimeout(10)
            .readTimeout(10)
            .connectTimeout(10)
            .addBuilderClientListener(object : OnBuildClientListener {
                override fun addBuildClient(): MutableSet<Any> {
                    return mutableSetOf(
                        GsonConverterFactory.create(),
                        RxJava2CallAdapterFactory.create()
                    )
                }
            })
            .build(this)

        btn_get.setOnClickListener {
            getRequest()
        }
        btn_post.setOnClickListener {
            postRequest()
        }

        btn_download.setOnClickListener {
            startActivity(Intent(this, DownLoadAct::class.java))
        }
        btn_upload.setOnClickListener {
            startActivity(Intent(this, UploadAct::class.java))
        }
    }

    private fun postRequest() {
        launch {
            rxHttp
                .create()
                .addParameter("title", "123456")
                .addParameter("author", "123456")
                .addParameter("link", "123456")
                .doPost("lg/collect/add/json", TestBean::class.java)
                .result(
                    {
                        log.text = it.toString()
                    },
                    {
                        log.text = it.message.toString()
                    }
                )
        }
    }


    private fun getRequest() {
        launch {
            rxHttp
                .create()
                .changeBaseUrl("http://localhost")
                .addParameter("k", "java")
                .doGet(parameter = "wxarticle/chapters/json", tClass = TestBean::class.java)
                .result({
                    Log.i(TAG, "success ${Thread.currentThread().name} info $it ")
                    log.text = it.toString()
                }, {
                    Log.i(TAG, "error ${Thread.currentThread().name} info ${it.toString()} ")
                    log.text = it.toString()
                })
        }
    }
}
