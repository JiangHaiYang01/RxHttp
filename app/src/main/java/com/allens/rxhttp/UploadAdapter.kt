package com.allens.rxhttp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder


class UploadAdapter(
    private val mData: List<String>,
    private val mRecyclerView: RecyclerView
) :
    RecyclerView.Adapter<UploadAdapter.MyViewHolder>() {

    override fun getItemCount(): Int {
        return mData.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvStatus.text = mData[position]
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
    }


    private var onBtnClickListener: OnBtnClickListener? = null

    interface OnBtnClickListener {
        fun onItemClickStart(info: String)
        fun onItemClickPause(info: String)
    }

    fun setOnBtnClickListener(listener: OnBtnClickListener) {
        onBtnClickListener = listener
    }


}