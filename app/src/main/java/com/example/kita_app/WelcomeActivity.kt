package com.example.kita_app

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.kita_app.databinding.ActivityWelcomeBinding
import com.google.android.material.navigation.NavigationView
import com.example.kita_app.databinding.NavHeaderBinding

class WelcomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityWelcomeBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var headerBinding: NavHeaderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setNavigationItemSelectedListener(this)

        // Get the username from the Intent
        val username = intent.getStringExtra("username") ?: "username"

        // Initialize header binding
        val headerView = binding.navView.getHeaderView(0)
        headerBinding = NavHeaderBinding.bind(headerView)

        // Set the username in the navigation header
        headerBinding.navHeaderSubtitle.text = "$username"

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = binding.drawerLayout

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_first_layout -> {
                // Handle the first layout navigation
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, FirstFragment())
                    .commit()
            }
            R.id.nav_second_layout -> {
                // Handle the second layout navigation
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, SecondFragment())
                    .commit()
            }
            R.id.nav_third_layout -> {
                // Handle the third layout navigation
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, ThirdFragment())
                    .commit()
            }
            R.id.nav_logout -> {
                // Handle the logout
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawers()
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(binding.navView)) {
            drawerLayout.closeDrawer(binding.navView)
        } else {
            super.onBackPressed()
        }
    }
}

