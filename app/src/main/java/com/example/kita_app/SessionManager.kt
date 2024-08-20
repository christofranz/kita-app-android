package com.example.kita_app

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("UserSessionPrefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_AUTH_TOKEN = "auth_token"
        const val KEY_LOGIN_TIME = "login_time"
    }

    // Save the token and the current login time
    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString(KEY_AUTH_TOKEN, token)
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
        editor.apply()
    }

    // Check if the user is logged in based on the token's validity
    fun isUserLoggedIn(): Boolean {
        val token = prefs.getString(KEY_AUTH_TOKEN, null)
        val loginTime = prefs.getLong(KEY_LOGIN_TIME, 0)

        return if (token != null) {
            val currentTime = System.currentTimeMillis()
            val twentyFourHoursInMillis = 24 * 60 * 60 * 1000

            (currentTime - loginTime) < twentyFourHoursInMillis
        } else {
            false
        }
    }

    // Log the user out by clearing the stored data
    fun logoutUser() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }
}
