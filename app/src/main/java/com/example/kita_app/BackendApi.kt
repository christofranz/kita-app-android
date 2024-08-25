package com.example.kita_app

import android.content.Context
import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object BackendApi {

    fun sendFcmTokenToServer(context: Context, token: String) {
        val retrofitInstance = RetrofitClient.getInstance(context) // Get the Retrofit instance
        val apiService = retrofitInstance.create(Api::class.java)

        val request = FcmTokenRequest(fcm_token = token)

        apiService.sendFcmToken(request).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("BackendApi", "FCM token sent successfully")
                } else {
                    Log.e("BackendApi", "Failed to send FCM token: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("BackendApi", "Error sending FCM token", t)
            }
        })
    }
}