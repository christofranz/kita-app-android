package com.example.kita_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kita_app.databinding.ActivityResetPasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResetPasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding.resetPasswordButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            //TODO: validate user input
            resetPassword(email)
        }
    }

    private fun resetPassword(email: String) {
        val passwordResetRequest = PasswordResetRequest(email)
        val apiService = RetrofitClient.getInstance(this).create(Api::class.java)
        apiService.resetPassword(passwordResetRequest).enqueue(object : Callback<ResponseMessage> {
            override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                if (response.isSuccessful) {
                    auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val message = response.body()?.message
                                Toast.makeText(this@ResetPasswordActivity, message, Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@ResetPasswordActivity, LoginActivity::class.java)
                                startActivity(intent)
                            } else {
                                // Reset failed, handle error
                                task.exception?.let { exception ->
                                    Toast.makeText( this@ResetPasswordActivity, "Failed to send reset email: ${exception.message}", Toast.LENGTH_LONG).show()
                                    Log.e("FirebaseAuth", "Error: ${exception.message}", exception)
                                }
                            }
                        }
                } else {
                    Toast.makeText(this@ResetPasswordActivity, "Reset password failed in backend", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                Toast.makeText(this@ResetPasswordActivity, t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }
}
