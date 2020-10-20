package com.allens.lib_http2.impl

import retrofit2.CallAdapter
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.prefs.PreferencesFactory

interface OnFactoryListener {

    fun addConverterFactory(): MutableSet<Converter.Factory>

    fun addCallAdapterFactory(): MutableSet<CallAdapter.Factory>? {
        return null
    }
}