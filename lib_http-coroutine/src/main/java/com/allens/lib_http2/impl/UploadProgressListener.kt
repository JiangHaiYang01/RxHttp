package com.allens.lib_http2.impl

interface UploadProgressListener{
    /**
     *
     * @param bytesWriting 已经写的字节数
     * @param totalBytes   文件的总字节数
     */
    fun onProgress(bytesWriting: Long, totalBytes: Long)
}