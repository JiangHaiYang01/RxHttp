package com.allens.lib_http2.download.utils

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import com.allens.lib_http2.manager.HttpManager

/**
 *
 * Created by allens on 2017/11/30.
 */
object ShareDownLoadUtil {


    private var path = Build.BRAND + "_" + Build.MODEL + "_" + "download_sp"
    private val sp: SharedPreferences


    init {
        sp = HttpManager.context.getSharedPreferences(path, Context.MODE_PRIVATE)
    }


    fun setPath(path: String) {
        ShareDownLoadUtil.path = path
    }

    fun putBoolean(key: String, value: Boolean) {
        sp.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return sp.getBoolean(key, defValue)
    }

    fun putString(key: String, value: String) {
        sp.edit().putString(key, value).apply()
    }

    fun getString(key: String, defValue: String): String? {
        return sp.getString(key, defValue)
    }

    fun putInt(key: String, value: Int) {
        sp.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defValue: Int): Int {
        return sp.getInt(key, defValue)
    }

    fun putLong(key: String?, value: Long) {
        sp.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defValue: Long): Long {
        return sp.getLong(key, defValue)
    }

    fun remove(key: String) {
        sp.edit().remove(key).apply()
    }

    fun clear() {
        sp.edit().clear().apply()
    }


}