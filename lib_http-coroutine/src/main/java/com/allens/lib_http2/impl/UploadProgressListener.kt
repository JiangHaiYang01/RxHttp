package com.allens.lib_http2.impl


interface OnUpLoadListener<T> : UploadProgressListener, UpLoadCancelListener {

    fun opUploadPrepare(tag: String)

    fun onUpLoadSuccess(tag: String, data: T)

    fun onUpLoadFailed(tag: String, throwable: Throwable)


}


interface UpLoadCancelListener {
    fun onUploadCancel(tag: String)
}

interface UploadProgressListener {
    /**
     *
     * @param tag          tag
     * @param bytesWriting 已经写的字节数
     * @param totalBytes   文件的总字节数
     * @param progress     进度
     */
    fun onUploadProgress(tag: String, bytesWriting: Long, totalBytes: Long, progress: Int)
}