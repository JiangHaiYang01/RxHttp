package com.allens.rxhttp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpLevel
import com.allens.lib_http2.impl.OnDownLoadListener
import kotlinx.android.synthetic.main.activity_dowload.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class DownLoadAct : AppCompatActivity(), CoroutineScope by MainScope(),
    MyAdapter.OnBtnClickListener, OnDownLoadListener {
    private lateinit var rxHttp: RxHttp
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dowload)


        rxHttp = RxHttp.Builder()
            .baseUrl("https://www.wanandroid.com")
            .isLog(true)
            .level(HttpLevel.BODY)
            .writeTimeout(10)
            .readTimeout(10)
            .connectTimeout(10)
            .build(this)


        val downloadUrl = listOf(
            "http://update.9158.com/miaolive/Miaolive.apk",  //喵播
            "https://apk-ssl.tancdn.com/3.5.3_276/%E6%8E%A2%E6%8E%A2.apk",  //探探
            "https://o8g2z2sa4.qnssl.com/android/momo_8.18.5_c1.apk",  //陌陌
            "http://s9.pstatp.com/package/apk/aweme/app_aweGW_v6.6.0_2905d5c.apk" //抖音
        )


        val data = mutableListOf<DownLoadInfo>()

        for ((index, info) in downloadUrl.withIndex()) {
            data.add(
                DownLoadInfo(info, info + "_" + index)
            )
        }


        val mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView.layoutManager = mLayoutManager
        val myAdapter = MyAdapter(data)
        mRecyclerView.adapter = myAdapter
        myAdapter.setOnBtnClickListener(this)
    }


    override fun onItemClick(info: DownLoadInfo) {
        launch {
            rxHttp.doDownLoad(info.taskId, info.url, "", "", this)
        }
       
    }

    override fun onDownLoadProgress(key: String, progress: Int) {
        TODO("Not yet implemented")
    }

    override fun onDownLoadError(key: String, throwable: Throwable) {
        TODO("Not yet implemented")
    }

    override fun onDownLoadSuccess(key: String, path: String) {
        TODO("Not yet implemented")
    }

    override fun onDownLoadPause(key: String) {
        TODO("Not yet implemented")
    }

    override fun onDownLoadCancel(key: String) {
        TODO("Not yet implemented")
    }

    override fun onUpdate(key: String, read: Long, count: Long, done: Boolean) {
        TODO("Not yet implemented")
    }


}


data class DownLoadInfo(val url: String, val taskId: String)