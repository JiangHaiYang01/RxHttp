package com.allens.lib_http2.impl


interface DownLoadProgressListener {
    /**
     * 下载进度
     *
     * @param key url
     * @param read  读取
     * @param count 总共长度
     * @param done  是否完成
     */
    fun onUpdate(
        key: String,
        read: Long,
        count: Long,
        done: Boolean
    )
}


interface OnDownLoadListener : DownLoadProgressListener {


    fun onDownLoadProgress(key: String, progress: Int)

    fun onDownLoadError(key: String, throwable: Throwable)

    fun onDownLoadSuccess(key: String, path: String)

    fun onDownLoadPause(key: String)

    fun onDownLoadCancel(key: String)
}