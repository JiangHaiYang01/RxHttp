package com.allens.lib_http2.impl

import retrofit2.Retrofit
import java.util.prefs.PreferencesFactory

interface OnBuildClientListener {

    fun addBuildClient(): MutableSet<Any>

}