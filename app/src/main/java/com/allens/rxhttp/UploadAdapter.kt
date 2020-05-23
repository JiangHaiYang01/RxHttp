package com.allens.rxhttp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.allens.lib_http2.download.utils.FileTool
import java.io.File


class UploadAdapter(
    private val mData: List<UpLoadInfo>,
    private val mRecyclerView: RecyclerView
) :
    RecyclerView.Adapter<UploadAdapter.MyViewHolder>() {

    override fun getItemCount(): Int {
        return mData.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvSize.text = "0.0/${FileTool.bytes2kb(File(mData[position].path).length())}"
        holder.btnStart.setOnClickListener {
            if (holder.btnStart.text == "开始")
                onBtnClickListener?.onItemClickStart(mData[position])
            else
                onBtnClickListener?.onItemClickPause(mData[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_download, parent, false)
        return MyViewHolder(view)
    }

    class MyViewHolder(itemView: View) : ViewHolder(itemView) {
        var tvStatus: TextView = itemView.findViewById(R.id.tv_waiting)
        var btnStart: Button = itemView.findViewById(R.id.bt_pause)
        var tvSize: TextView = itemView.findViewById(R.id.tv_size)
    }


    private var onBtnClickListener: OnBtnClickListener? = null

    interface OnBtnClickListener {
        fun onItemClickStart(info: UpLoadInfo)
        fun onItemClickPause(info: UpLoadInfo)
    }

    fun setOnBtnClickListener(listener: OnBtnClickListener) {
        onBtnClickListener = listener
    }

    fun uploadFailed(key: String, throwable: Throwable) {
        getChildAt(key)?.findViewById<TextView>(R.id.tv_waiting)?.text = "上传失败"
        getChildAt(key)?.findViewById<TextView>(R.id.bt_pause)?.text = "开始"
    }

    fun uploadProgress(key: String, progress: Int, bytes2kb: String, bytes2kb1: String) {
        getChildAt(key)?.findViewById<TextView>(R.id.bt_pause)?.text = "暂停"
        getChildAt(key)?.findViewById<TextView>(R.id.tv_size)?.text = "$bytes2kb/$bytes2kb1"
        getChildAt(key)?.findViewById<TextView>(R.id.tv_waiting)?.text = "正在上传"
        getChildAt(key)?.findViewById<TextView>(R.id.tv_progress)?.text = "$progress%"
        getChildAt(key)?.findViewById<ProgressBar>(R.id.progress_bar)?.progress = progress
    }

    fun uploadSuccess(key: String, data: TestBean) {
        getChildAt(key)?.findViewById<TextView>(R.id.tv_waiting)?.text = "上传成功"
        getChildAt(key)?.findViewById<TextView>(R.id.bt_pause)?.text = "开始"
    }


    private fun getChildAt(key: String): View? {
        for ((index, data) in mData.withIndex()) {
            if (data.taskId == key) {
                return mRecyclerView.getChildAt(index)
            }
        }
        return null
    }


}