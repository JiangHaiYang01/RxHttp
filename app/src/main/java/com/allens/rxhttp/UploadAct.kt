package com.allens.rxhttp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.allens.lib_http2.RxHttp
import com.allens.lib_http2.config.HttpLevel
import com.allens.lib_http2.download.utils.FileTool
import com.allens.lib_http2.impl.OnUpLoadListener
import kotlinx.android.synthetic.main.activity_upload.mRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import me.rosuh.filepicker.config.FilePickerManager
import java.io.File


class UploadAct : AppCompatActivity(), CoroutineScope by MainScope(),
    UploadAdapter.OnBtnClickListener, OnUpLoadListener<TestBean>{

    private lateinit var rxHttp: RxHttp

    private lateinit var myAdapter: UploadAdapter

    private val upLoadList = mutableListOf<UpLoadInfo>()

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
            .connectTimeout(10)
            .build(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CUSTOM_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    upLoadList.clear()
                    val list = FilePickerManager.obtainData()
                    for (path in list) {
                        upLoadList.add(UpLoadInfo(path, path + "_" + "taskId"))
                    }

                    val mLayoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    mRecyclerView.layoutManager = mLayoutManager
                    myAdapter = UploadAdapter(upLoadList, mRecyclerView)
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu == null)
            return false
        menu.add(1, 1, 1, "选择文件")
        menu.add(1, 2, 2, "全部开始")
        menu.add(1, 3, 2, "全部取消")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                FilePickerManager
                    .from(activity = this)
                    .forResult(CUSTOM_REQUEST_CODE)
            }
            2 -> {
                for (data in upLoadList) {
                    launch {
                        startUploadSuspend(data)
                    }
                }
            }
            3 -> {
                for (data in upLoadList) {
                    rxHttp.create().doUpLoadCancel(data.taskId)
                }
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onItemClickCancel(info: UpLoadInfo) {
        rxHttp.create().doUpLoadCancel(info.taskId)
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
            "progress ----> bytesWriting ${  FileTool.bytes2kb(bytesWriting)} " +
                    "totalBytes ${FileTool.bytes2kb(
                totalBytes
            )} progress $progress  thread ${Thread.currentThread().name}"
        )
        myAdapter.uploadProgress(
            tag,
            progress,
            FileTool.bytes2kb(bytesWriting),
            FileTool.bytes2kb(totalBytes)
        )
    }

    override fun onUploadCancel(tag: String) {
        myAdapter.uploadCancel(tag)
    }

    override fun onUpLoadSuccess(tag: String, data: TestBean) {
        Log.i(TAG, "data ----> $data thread ${Thread.currentThread().name}")
        myAdapter.uploadSuccess(tag, data)
    }

    override fun opUploadPrepare(tag: String) {
        Log.i(TAG, "opUploadPrepare ----> thread ${Thread.currentThread().name}")
        myAdapter.uploadPrepare(tag)
    }

    override fun onDestroy() {
        super.onDestroy()
        for (data in upLoadList) {
            rxHttp.create().doUpLoadCancel(data.taskId)
        }
    }
}


data class UpLoadInfo(val path: String, val taskId: String)