package com.allens.lib_http2.upload

import androidx.annotation.Nullable
import com.allens.lib_http2.impl.UploadProgressListener
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException


class ProgressRequestBody(
    private val retrofitProgressUploadListener: UploadProgressListener,
    private val requestBody: RequestBody
) :
    RequestBody() {
    private var bufferedSink: BufferedSink? = null

    @Nullable
    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    @Throws(IOException::class)
    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    //关键方法
    @Throws(IOException::class)
    override fun writeTo(sink: BufferedSink) {
        if (null == bufferedSink) bufferedSink = sink(sink).buffer()
        requestBody.writeTo(bufferedSink!!)
        //必须调用flush，否则最后一部分数据可能不会被写入
        bufferedSink!!.flush()
    }

    private fun sink(sink: Sink): Sink {
        return object : ForwardingSink(sink) {
            var bytesWriting = 0L
            var contentLength = 0L

            @Throws(IOException::class)
            override fun write(source: Buffer, byteCount: Long) {
                super.write(source, byteCount)
                if (0L == contentLength) contentLength = contentLength()
                bytesWriting += byteCount
                //调用接口，把上传文件的进度传过去
                retrofitProgressUploadListener.onProgress(bytesWriting, contentLength)
            }
        }
    }

}