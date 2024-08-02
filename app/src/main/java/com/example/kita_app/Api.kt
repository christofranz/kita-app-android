package com.example.kita_app
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {
    @POST("register")
    fun register(@Body user: User): Call<ResponseMessage>

    @POST("login")
    fun login(@Body user: User): Call<ResponseMessage>
}
