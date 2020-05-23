package com.allens.lib_http2.impl


interface DownLoadProgressListener {
    /**
     * 下载进度
     *
     * @param key url
     * @param progress  进度
     * @param read  读取
     * @param count 总共长度
     * @param done  是否完成
     */
    fun onUpdate(
        key: String,
        progress: Int,
        read: Long,
        count: Long,
        done: Boolean
    )
}


interface OnDownLoadListener : DownLoadProgressListener {


    //等待下载
    fun onDownLoadPrepare(key: String)

    //进度
    fun onDownLoadProgress(key: String, progress: Int)

    //下载失败
    fun onDownLoadError(key: String, throwable: Throwable)

    //下载成功
    fun onDownLoadSuccess(key: String, path: String)

    //下载暂停
    fun onDownLoadPause(key: String)

    //下载取消
    fun onDownLoadCancel(key: String)
}