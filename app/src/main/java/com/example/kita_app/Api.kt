package com.example.kita_app
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class SetRoleRequest(val admin_username: String, val target_username: String, val new_role: String)


interface Api {
    @POST("register")
    fun register(@Body user: User): Call<ResponseMessage>

    @POST("login")
    fun login(@Body user: User): Call<LoginResponse>

    @POST("set_role")
    fun setRole(@Body request: SetRoleRequest): Call<ResponseMessage>
}
