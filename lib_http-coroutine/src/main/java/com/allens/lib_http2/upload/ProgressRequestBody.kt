package com.allens.lib_http2.upload

import android.os.Handler
import androidx.annotation.Nullable
import com.allens.lib_http2.impl.OnUpLoadListener
import com.allens.lib_http2.impl.UploadProgressListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.*
import java.io.IOException


class ProgressRequestBody(
    private val listener: UploadProgressListener?,
    private val tag: String,
    private val requestBody: RequestBody,
    private val handler: Handler?
) :
    RequestBody() {
    private var bufferedSink: BufferedSink? = null

    private var lastProgress: Int = 0


    fun getRequestBody(): RequestBody {
        return requestBody
    }

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
                val progress = (bytesWriting.toFloat() / contentLength * 100).toInt() // 计算百分比
                if (lastProgress != progress) {
                    lastProgress = progress
                    handler?.post {
                        listener?.onUploadProgress(
                            tag,
                            bytesWriting,
                            contentLength,
                            progress
                        )
                    }

                }
            }
        }
    }

}