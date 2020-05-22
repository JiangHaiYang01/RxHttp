package com.allens.lib_http2.impl

import kotlinx.coroutines.Deferred
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
interface ApiService {
    @GET
    suspend fun doGet(
        @HeaderMap headers: HashMap<String, String>,
        @Url url: String
    ): Response<ResponseBody>

    @FormUrlEncoded
    @POST("{path}")
    suspend fun doPost(
        @Path(
            value = "path",
            encoded = true
        ) urlPath: String,
        @HeaderMap headers: HashMap<String, String>,
        @FieldMap map: HashMap<String, Any>
    ): Response<ResponseBody>


    @POST("{path}")
    suspend fun doBody(
        @Path(
            value = "path",
            encoded = true
        ) urlPath: String, @HeaderMap headers: HashMap<String, String>, @Body body: RequestBody
    ): Response<ResponseBody>


    @DELETE
    suspend fun doDelete(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @QueryMap maps: Map<String, Any>
    ): Response<ResponseBody>


    @PUT
    suspend fun doPut(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @FieldMap maps: HashMap<String, Any>
    ): Response<ResponseBody>

    @Streaming
    @GET
    suspend fun downloadFile(
        @Header("RANGE") start: String,
        @Url url: String
    ): Response<ResponseBody>


    @Multipart
    @POST
    fun upload(
        @Url url: String,
        @HeaderMap headers: HashMap<String, String>,
        @QueryMap maps: Map<String, Any>,
        @PartMap map: Map<String, RequestBody>
    ): Response<ResponseBody>

}