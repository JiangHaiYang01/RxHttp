package com.allens.rxhttp

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpLevel
import com.allens.lib_http2.impl.OnDownLoadListener
import kotlinx.android.synthetic.main.activity_dowload.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File


class DownLoadAct : AppCompatActivity(), CoroutineScope by MainScope(),
    MyAdapter.OnBtnClickListener, OnDownLoadListener {
    private lateinit var rxHttp: RxHttp
    private lateinit var data: MutableList<DownLoadInfo>

    companion object {
        const val TAG = "TAG"
    }

    private lateinit var myAdapter: MyAdapter
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
            "https://o8g2z2sa4.qnssl.com/android/momo_8.18.5_c1.apk" //抖音
        )


        data = mutableListOf<DownLoadInfo>()

        for ((index, info) in downloadUrl.withIndex()) {
            data.add(
                DownLoadInfo(info, info + "_" + index,"$index.apk")
            )
        }


        val mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        mRecyclerView.layoutManager = mLayoutManager
        myAdapter = MyAdapter(data, mRecyclerView)
        mRecyclerView.adapter = myAdapter
        myAdapter.setOnBtnClickListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu == null)
            return false
        menu.add(1, 1, 1, "全部开始")
        menu.add(1, 2, 2, "全部暂停")
        menu.add(1, 3, 2, "全部取消")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                for (info in data) {
                    launch {
                        startDownLoad(info)
                    }
                }
            }
            2 -> {
                rxHttp.doDownLoadPauseAll()
            }
            3 -> {
                rxHttp.doDownLoadCancelAll()
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onItemClickStart(info: DownLoadInfo) {
        launch {
            startDownLoad(info)
        }

    }

    override fun onItemClickPause(downLoadInfo: DownLoadInfo) {
        launch {
            pauseDownLoad(downLoadInfo);
        }
    }

    private suspend fun startDownLoad(info: DownLoadInfo) {
        rxHttp.doDownLoad(info.taskId, info.url, getBasePath(this), info.saveName, this)
    }

    private fun pauseDownLoad(info: DownLoadInfo) {
        rxHttp.doDownLoadPause(info.taskId)
    }

    override fun onDownLoadPrepare(key: String) {
        Log.i(TAG, "准备下载 $key  thread ${Thread.currentThread().name}")
        myAdapter.setDownLoadPrepare(key)
    }


    override fun onDownLoadProgress(key: String, progress: Int) {
        Log.i(TAG, "下载进度 $progress  thread ${Thread.currentThread().name}")

    }

    override fun onDownLoadError(key: String, throwable: Throwable) {
        Log.i(TAG, "下载失败 ${throwable.message}  thread ${Thread.currentThread().name}")
        myAdapter.setDownLoadError(key, throwable)
    }

    override fun onDownLoadSuccess(key: String, path: String) {
        Log.i(TAG, "下载成功 $key  thread ${Thread.currentThread().name}")
        myAdapter.setDownLoadSuccess(key, path)
    }

    override fun onDownLoadPause(key: String) {
        Log.i(TAG, "下载暂停 $key  thread ${Thread.currentThread().name}")
        myAdapter.setDownLoadPause(key)
    }

    override fun onDownLoadCancel(key: String) {
        Log.i(TAG, "下载取消 $key  thread ${Thread.currentThread().name}")
        myAdapter.setDownLoadCancel(key)
    }

    override fun onUpdate(key: String, progress: Int, read: Long, count: Long, done: Boolean) {
        Log.i(
            TAG,
            "下载进度 $key  read $read  count $count  done $done thread ${Thread.currentThread().name}"
        )
        myAdapter.setDownLoadProgress(
            key,
            progress,
            rxHttp.bytes2kb(read),
            rxHttp.bytes2kb(count),
            done
        )
    }


    //获取更路径
    private fun getBasePath(context: Context): String {
        var p: String = Environment.getExternalStorageDirectory().path
        val f: File? = context.getExternalFilesDir(null)
        if (null != f) {
            p = f.absolutePath
        }
        return p
    }


}


data class DownLoadInfo(val url: String, val taskId: String, val saveName: String)