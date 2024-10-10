package com.example.kita_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kita_app.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            //TODO: validate input + trim
            loginUser(email, password)
        }

        binding.registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.resetPasswordButton.setOnClickListener {
            val intent = Intent(this, ResetPasswordActivity::class.java)
            startActivity(intent)
        }

        sessionManager = SessionManager(this)
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        user.getIdToken(true).addOnCompleteListener { tokenTask ->
                            if (tokenTask.isSuccessful) {
                                val firebaseIdToken = tokenTask.result?.token

                                if (firebaseIdToken != null) {
                                    val apiService = RetrofitClient.getInstance(this).create(Api::class.java)
                                    val login = LoginRequest(firebaseIdToken)
                                    apiService.login(login).enqueue(object : Callback<LoginResponse> {
                                        override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                                            if (response.isSuccessful) {
                                                val loginResponse = response.body()
                                                if (loginResponse != null) {
                                                    Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()
                                                    RetrofitClient.setToken(loginResponse.token)
                                                    sessionManager.saveToken(loginResponse.token)

                                                    val role = loginResponse.user.role
                                                    // TODO: consistens useage intent vs. shared preferences
                                                    val intent = Intent(this@LoginActivity, WelcomeActivity::class.java).apply {
                                                        putExtra("username", loginResponse.user.first_name) //TODO: replace username with first name
                                                        putExtra("role", role)
                                                    }
                                                    // Save the user id in EncryptedSharedPreferences
                                                    val sharedPreferences = getEncryptedSharedPreferences(this@LoginActivity)
                                                    val editor = sharedPreferences.edit()
                                                    editor.putString("user_id", loginResponse.user.id)
                                                    editor.apply()
                                                    // share FCM token with backend
                                                    MyFirebaseMessagingService.requestToken(this@LoginActivity)
                                                    startActivity(intent)
                                                    finish()
                                                } else {
                                                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                                                }
                                            } else {
                                                Toast.makeText(this@LoginActivity, "Invalid credentials", Toast.LENGTH_SHORT).show()
                                            }
                                        }

                                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                                            Toast.makeText(this@LoginActivity, t.message, Toast.LENGTH_SHORT).show()
                                        }
                                    })
                                }
                            }
                        }
                    } else {
                        // Email is not verified
                        Toast.makeText(this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show()
                        auth.signOut()  // Log out the user
                    }
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
