package com.example.kita_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kita_app.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username")
        binding.welcomeTextView.text = "Welcome, $username!"
    }
}
