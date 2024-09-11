package com.example.kita_app
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.Path

data class SetRoleRequest(val target_username: String, val new_role: String)
data class FcmTokenRequest(val fcm_token: String)
data class Event(
    val _id: String,  // Event ID
    val classroom: String,
    val date: String,
    val event_type: String,
    val max_children_allowed: Int,
    val children_staying_home: List<String>
)
data class ChildEvents(
    val child_name: String,
    val group_name: String,
    val events: List<Event>
)
data class Feedback(
    val child_id: String
)


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

    @GET("/events")
    fun getUpcomingEvents(): Call<List<ChildEvents>>

    @POST("/events/{event_id}/feedback")
    fun postEventFeedback(@Path("event_id") eventId: String, @Body feedback: Feedback): Call<ResponseMessage>


}
