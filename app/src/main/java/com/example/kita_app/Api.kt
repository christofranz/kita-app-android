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
    val child_id: String,
    val child_name: String,
    val classroom: String,
    val events: List<Event>
)
data class Feedback(
    val child_id: String
)

data class FeedbackResponse(
    val staying_home: Boolean
)


interface Api {
    @POST("register")
    fun register(@Body user: RegisterUser): Call<ResponseMessage>

    @POST("login")
    fun login(@Body login: LoginRequest): Call<LoginResponse>

    @POST("reset_password")
    fun resetPassword(@Body email: PasswordResetRequest): Call<ResponseMessage>

    @POST("set_role")
    fun setRole(@Body request: SetRoleRequest): Call<ResponseMessage>

    @GET("/protected")
    fun getProtected(): Call<ResponseMessage>

    @POST("/register_fcm_token")
    fun sendFcmToken(@Body request: FcmTokenRequest): Call<Void>

    @GET("/user/{user_id}/events")
    fun getUpcomingEvents(@Path("user_id") user_id: String): Call<List<ChildEvents>>

    @POST("/events/{event_id}/feedback")
    fun postEventFeedback(@Path("event_id") eventId: String, @Body feedback: Feedback): Call<ResponseMessage>

    @GET("/events/{event_id}/feedback/{child_id}")
    fun getFeedback(@Path("event_id") eventId: String, @Path("child_id") child_id: String): Call<FeedbackResponse>

    @POST("/events/{event_id}/feedback/{child_id}/withdraw")
    fun withdrawFeedback(@Path("event_id") eventId: String, @Path("child_id") child_id: String): Call<FeedbackResponse>
}
