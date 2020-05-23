package com.allens.rxhttp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpLevel
import com.allens.lib_http2.impl.OnLogListener
import com.allens.lib_http2.impl.OnUpLoadListener
import com.allens.lib_http2.impl.UploadProgressListener
import kotlinx.android.synthetic.main.activity_dowload.*
import kotlinx.android.synthetic.main.activity_upload.*
import kotlinx.android.synthetic.main.activity_upload.mRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import me.rosuh.filepicker.config.FilePickerManager
import java.io.File


class UploadAct : AppCompatActivity(), CoroutineScope by MainScope(),
    UploadAdapter.OnBtnClickListener, OnLogListener, OnUpLoadListener<TestBean> {

    private lateinit var rxHttp: RxHttp

    private lateinit var myAdapter: UploadAdapter

    companion object {
        const val CUSTOM_REQUEST_CODE = 1
        const val TAG = "Upload"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)


        rxHttp = RxHttp.Builder()
            .baseUrl("https://www.wanandroid.com")
            .isLog(true)
            .level(HttpLevel.BODY)
            .writeTimeout(10)
            .readTimeout(10)
            .addLogListener(this)
            .connectTimeout(10)
            .build(this)


        btn_select_file.setOnClickListener {
            FilePickerManager
                .from(activity = this)
                .forResult(CUSTOM_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CUSTOM_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val list = FilePickerManager.obtainData()
                    val pathList = mutableListOf<UpLoadInfo>()
                    for (path in list) {
                        pathList.add(UpLoadInfo(path, path + "_" + "taskId"))
                    }

                    val mLayoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    mRecyclerView.layoutManager = mLayoutManager
                    myAdapter = UploadAdapter(pathList, mRecyclerView)
                    mRecyclerView.adapter = myAdapter
                    myAdapter.setOnBtnClickListener(this)
                } else {
                    Toast.makeText(this, "没有选择任何东西~", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun startUploadSuspend(info: UpLoadInfo) {
        rxHttp.create()
            .addFile("uploaded_file", File(info.path))
            .addHeard("heard", "1")
            .addParameter("parameter", "2")
            .doUpload(
                info.taskId,
                "http://t.xinhuo.com/index.php/Api/Pic/uploadPic",
                TestBean::class.java,
                this
            )
    }

    override fun onItemClickStart(info: UpLoadInfo) {
        Log.i(TAG, "上传 $info")
        launch {
            startUploadSuspend(info)
        }
    }

    override fun onItemClickPause(info: UpLoadInfo) {
    }

    override fun onRxHttpLog(message: String) {
    }

    override fun onUpLoadFailed(tag: String, throwable: Throwable) {
        Log.i(TAG, "failed ----> $throwable thread ${Thread.currentThread().name}")
        myAdapter.uploadFailed(tag, throwable)
    }

    override fun onUploadProgress(
        tag: String,
        bytesWriting: Long,
        totalBytes: Long,
        progress: Int
    ) {
        Log.i(
            TAG,
            "progress ----> bytesWriting ${rxHttp.bytes2kb(bytesWriting)} totalBytes ${rxHttp.bytes2kb(
                totalBytes
            )} progress $progress  thread ${Thread.currentThread().name}"
        )
        myAdapter.uploadProgress(
            tag,
            progress,
            rxHttp.bytes2kb(bytesWriting),
            rxHttp.bytes2kb(totalBytes)
        )
    }

    override fun onUpLoadSuccess(tag: String, data: TestBean) {
        Log.i(TAG, "data ----> $data thread ${Thread.currentThread().name}")
        myAdapter.uploadSuccess(tag, data)
    }
}


data class UpLoadInfo(val path: String, val taskId: String)