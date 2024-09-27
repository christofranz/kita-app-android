package com.example.kita_app

data class User(
    val id: String,
    val email: String,
    val first_name: String,
    val last_name: String,
    val role: String
)

data class RegisterUser(
    val firebase_id_token: String,
    val first_name: String,
    val last_name: String,
    val role: String
)

data class LoginResponse(
    val token: String,
    val message: String,
    val user: User
)
