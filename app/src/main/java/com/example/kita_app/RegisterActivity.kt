package com.example.kita_app

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kita_app.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.firebase.auth.FirebaseAuth
import android.util.Log

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            val first_name = binding.firstNameEditText.text.toString()
            val last_name = binding.lastNameEditText.text.toString()
            val role = binding.roleEditText.text.toString()
            //TODO: validate user input
            registerUser(email, password, first_name, last_name, role)
        }
    }

    private fun registerUser(email: String, password: String, first_name: String, last_name: String, role: String) {

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        // Send email verification
                        it.sendEmailVerification().addOnCompleteListener { emailTask ->
                            if (emailTask.isSuccessful) {
                                it.getIdToken(true).addOnCompleteListener { tokenTask ->
                                    if (tokenTask.isSuccessful) {
                                        val firebaseIdToken = tokenTask.result?.token
                                        if (firebaseIdToken != null) {
                                            // register user in backend
                                            val apiService = RetrofitClient.getInstance(this).create(Api::class.java)
                                            val user = RegisterUser(firebaseIdToken, first_name, last_name, role)
                                            apiService.register(user).enqueue(object : Callback<ResponseMessage> {
                                                override fun onResponse(call: Call<ResponseMessage>, response: Response<ResponseMessage>) {
                                                    if (response.isSuccessful) {
                                                        val message = response.body()?.message
                                                        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
                                                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                                                        startActivity(intent)
                                                    } else {
                                                        Toast.makeText(this@RegisterActivity, "Registration failed", Toast.LENGTH_SHORT).show()
                                                    }
                                                }

                                                override fun onFailure(call: Call<ResponseMessage>, t: Throwable) {
                                                    Toast.makeText(this@RegisterActivity, t.message, Toast.LENGTH_SHORT).show()
                                                }
                                            })
                                        } else {
                                            // Handle error when receiving token
                                            Toast.makeText(this, "Failed to retrieve firebase token.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                                // Inform the user that the email has been sent
                                Toast.makeText(this, "Verification email sent to ${it.email}", Toast.LENGTH_SHORT).show()
                            } else {
                                // Handle error when sending the email
                                Toast.makeText(this, "Failed to send verification email.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    // Registration failed, handle error
                    task.exception?.let { exception ->
                        Toast.makeText(this, "Registration failed: ${exception.message}", Toast.LENGTH_LONG).show()
                        Log.e("FirebaseAuth", "Error: ${exception.message}", exception)
                    }
                }
            }
    }
}
