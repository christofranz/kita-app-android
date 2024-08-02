package com.example.kita_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kita_app.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            loginUser(username, password)
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(username: String, password: String) {
        val user = User(username, password)

        RetrofitClient.instance.login(user).enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                if (response.isSuccessful) {
                    val message = response.body()?.message
                    Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, WelcomeActivity::class.java).apply {
                        putExtra("username", username)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
