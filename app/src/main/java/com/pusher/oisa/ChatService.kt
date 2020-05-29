package com.pusher.oisa

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.POST
import retrofit2.http.Body

interface ChatService {
    @POST("/message")
    fun postMessage(@Body body:Message): Call<String>

    @POST("/login")
    fun postLogin(@Body body:Login): Call<Void>

    @POST("/create_account")
    fun postCreateAccount(@Body body:CreateAccount): Call<Void>

    @POST("/save_history")
    fun postSendHistory(@Body body:SaveHistory): Call<Void>

    @POST("/load_history")
    fun postLoadHistory(@Body body:LoadHistory): Call<History>

    companion object {
        private const val BASE_URL = "http://94.75.29.199:8000/"

        fun create(): ChatService {
            val retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
            return retrofit.create(ChatService::class.java)
        }
    }
}