package com.allens.rxhttp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpLevel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

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
            val data = rxHttp
                .create()
                .addParameter("title", "123456")
                .addParameter("author", "123456")
                .addParameter("link", "123456")
                .doPost("lg/collect/add/json", TestBean::class.java)
            rxHttp.checkResult(data, {
                log.text = it.toString()
            }, {
                log.text = it.toString()
            })
        }
    }


    private fun getRequest() {
        launch {
            Log.i(TAG, "get 方法启动 线程 ${Thread.currentThread().name}")
            val data = rxHttp
                .create()
                .addParameter("k", "java")
                .doGet(parameter = "wxarticle/chapters/json", tClass = TestBean::class.java)

            Log.i(TAG, "收到响应 $data thread ${Thread.currentThread().name}")
            rxHttp.checkResult(data, {
                Log.i(TAG, "success ${Thread.currentThread().name} info $it ")
                log.text = it.toString()
            }, {
                Log.i(TAG, "error ${Thread.currentThread().name} info ${it.toString()} ")
                log.text = it.toString()
            })
        }
    }
}
