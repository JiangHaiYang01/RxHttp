package com.allens.rxhttp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpLevel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    companion object {
        const val TAG = "Main"
    }

    private fun getBaseUrl(): String {
        return "https://www.wanandroid.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linear.addView(Button(this).apply {
            text = "日志"
            setOnClickListener {
                doLog()
            }
        })

        linear.addView(Button(this).apply {
            text = "GET请求"
            setOnClickListener {
                doGet()
            }
        })

        linear.addView(Button(this).apply {
            text = "表单请求"
            setOnClickListener {
                doForm()
            }
        })

        linear.addView(Button(this).apply {
            text = "Post请求"
            setOnClickListener {
                doPOST()
            }
        })

        linear.addView(Button(this).apply {
            text = "修改单个请求BaseUrl"
            setOnClickListener {
                doChangeBaseUrl()
            }
        })

        linear.addView(Button(this).apply {
            text = "修改单个请求连接超时"
            setOnClickListener {
                doChangeConnectTimeOut()
            }
        })

        linear.addView(Button(this).apply {
            text = "为所有请求添加请求头"
            setOnClickListener {
                doAddHeard()
            }
        })


        linear.addView(Button(this).apply {
            text = "下载"
            setOnClickListener {
                startActivity(Intent(this@MainActivity,DownLoadAct::class.java))
            }
        })

        linear.addView(Button(this).apply {
            text = "上传"
            setOnClickListener {
                startActivity(Intent(this@MainActivity,UploadAct::class.java))
            }
        })
    }

    private fun doAddHeard(){
        launch {
            val rxHttp = RxHttp.Builder()
                .baseUrl(getBaseUrl()) //base url
                .isDebug(true)  //是否打印log
                .addHead("name","allens")//为所有请求添加请求头
                .level(HttpLevel.BODY)
                .build(this@MainActivity)
            rxHttp.create()
                .doBody("lg/collect/add/json", TestBean::class.java)
                .doSuccess { println("success") }
                .doFailed { println("failed " + it.message) }

            rxHttp.create()
                .doBody("lg/collect/add/json", TestBean::class.java)
                .doSuccess { println("success") }
                .doFailed { println("failed " + it.message) }
            println("finish")
        }
    }

    private fun doLog() {
        launch {
            val rxHttp = RxHttp.Builder()
                .baseUrl(getBaseUrl()) //base url
                .isDebug(true)  //是否打印log
                .addLogInterceptor(MyLog())
                .level(HttpLevel.BODY)
                .build(this@MainActivity)
            rxHttp.create()
                .addParameter("link", "123456")
                .doBody("lg/collect/add/json", TestBean::class.java)
                .doSuccess { println("success") }
                .doFailed { println("failed " + it.message) }
            println("finish")
        }
    }

    private fun doChangeConnectTimeOut() {
        launch {
            val rxHttp = RxHttp.Builder()
                .baseUrl(getBaseUrl()) //base url
                .isDebug(true)  //是否打印log
                .build(this@MainActivity)
            rxHttp.create()
                .addParameter("link", "123456")
                .dynamicConnectTimeOut(1,TimeUnit.MILLISECONDS)
                .dynamicWriteTimeOut(1,TimeUnit.MILLISECONDS)
                .dynamicReadTimeOut(1,TimeUnit.MILLISECONDS)
                .doBody("lg/collect/add/json", TestBean::class.java)
                .doSuccess { println("success") }
                .doFailed { println("failed " + it.message) }
            println("finish")
        }
    }

    private fun doChangeBaseUrl() {
        launch {
            val rxHttp = RxHttp.Builder()
                .baseUrl(getBaseUrl()) //base url
                .isDebug(true)  //是否打印log
                .build(this@MainActivity)

            rxHttp.create()
                .addParameter("link", "123456")
                .dynamicBaseUrl("http://localhost")
                .doBody("lg/collect/add/json", TestBean::class.java)
                .doSuccess { println("success") }
                .doFailed { println("failed " + it.message) }
            println("finish")
        }
    }

    private fun doPOST(){
        launch {
            //初始化
            val rxHttp = RxHttp.Builder()
                .baseUrl(getBaseUrl()) //base url
                .build(this@MainActivity)

            rxHttp.create()
                .addParameter("title", "123456")
                .addParameter("author", "123456")
                .addParameter("link", "123456")
                .doBody("lg/collect/add/json", TestBean::class.java)
                .doSuccess { println("success") }
                .doFailed { println("failed " + it.message) }
            println("finish")
        }
    }

    private fun doForm() {
        launch {
            //初始化
            val rxHttp = RxHttp.Builder()
                .baseUrl(getBaseUrl()) //base url
                .build(this@MainActivity)

            rxHttp.create()
                .addParameter("title", "123456")
                .addParameter("author", "123456")
                .addParameter("link", "123456")
                .doPost("lg/collect/add/json", TestBean::class.java)
                .doSuccess { println("success") }
                .doFailed { println("failed " + it.message) }
            println("finish")
        }
    }


    private fun doGet() {
        launch {
            //初始化
            val rxHttp = RxHttp.Builder()
                .baseUrl(getBaseUrl()) //base url
                .isDebug(true)  //是否打印log
                .build(this@MainActivity)

            rxHttp.create()
//                .addParameter("firstName", "江")
//                .addParameter("lastName", "海洋")
//                .addHeard("heardFirst", "hello")
//                .addHeard("heardLast", "world")
                .doGet("wxarticle/chapters/json", TestBean::class.java)
                .doSuccess { println("success") }
                .doFailed { println("failed " + it.message) }
            println("finish")
        }
    }
}
