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
import kotlinx.android.synthetic.main.activity_dowload.*
import kotlinx.android.synthetic.main.activity_upload.*
import kotlinx.android.synthetic.main.activity_upload.mRecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import me.rosuh.filepicker.config.FilePickerManager
import java.io.File


class UploadAct : AppCompatActivity(), CoroutineScope by MainScope(),
    UploadAdapter.OnBtnClickListener {

    private lateinit var rxHttp: RxHttp

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
                    val mLayoutManager =
                        LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    mRecyclerView.layoutManager = mLayoutManager
                    val myAdapter = UploadAdapter(list, mRecyclerView)
                    mRecyclerView.adapter = myAdapter
                    myAdapter.setOnBtnClickListener(this)
                } else {
                    Toast.makeText(this, "没有选择任何东西~", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun startUpload(path: String) {
        launch {
            val request = rxHttp.create()
//                .addFile("uploaded_file", File(path))
                .addHeard("heard","1")
                .addParameter("parameter","2")
                .doUpload("http://t.xinhuo.com/index.php/Api/Pic/uploadPic", TestBean::class.java)
            rxHttp.checkResult(request, {
                Log.i(TAG, "上传成功 ${it.toString()}")
            }, {
                Log.i(TAG, "上传失败 $it")
            })
        }

    }

    override fun onItemClickStart(info: String) {
        Log.i(TAG, "上传 $info")
        startUpload(info)
    }

    override fun onItemClickPause(info: String) {
    }
}