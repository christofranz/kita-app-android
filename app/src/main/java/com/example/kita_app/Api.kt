package com.example.kita_app
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET

data class SetRoleRequest(val target_username: String, val new_role: String)
data class FcmTokenRequest(val fcm_token: String)

interface Api {
    @POST("register")
    fun register(@Body user: User): Call<ResponseMessage>

    @POST("login")
    fun login(@Body user: User): Call<LoginResponse>

    @POST("set_role")
    fun setRole(@Body request: SetRoleRequest): Call<ResponseMessage>

    @GET("/protected")
    fun getProtected(): Call<ResponseMessage>

    @POST("/register_fcm_token")
    fun sendFcmToken(@Body request: FcmTokenRequest): Call<Void>
}
