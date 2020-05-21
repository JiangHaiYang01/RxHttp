package com.allens.lib_http2.tools

object UrlTool {
    //拼接URL
    fun prepareParam(paramMap: Map<String, Any>): String {
        val sb = StringBuilder()
        return if (paramMap.isEmpty()) {
            ""
        } else {
            for (key in paramMap.keys) {
                val value = paramMap[key]
                if (sb.isEmpty()) {
                    sb.append(key).append("=").append(value)
                } else {
                    sb.append("&").append(key).append("=").append(value)
                }
            }
            sb.toString()
        }
    }
}