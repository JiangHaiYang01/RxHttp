package com.allens.rxhttp

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


/**
 *
 * @Description:
 * @Author:         Allens
 * @CreateDate:     2019-11-22 11:47
 * @Version:        1.0
 */
interface CustomApiService {


    @GET
    fun doGet(
        @Url url: String
    ): Observable<TestBean>


}