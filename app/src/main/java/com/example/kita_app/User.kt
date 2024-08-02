package com.example.kita_app

data class User(
    val username: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val message: String
)
