package com.allens.lib_http2.impl

import retrofit2.Retrofit

interface OnBuildClientListener {

    fun addBuildClient(client: Retrofit.Builder)

}