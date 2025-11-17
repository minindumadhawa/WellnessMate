package com.example.wellnessmate

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.wellnessmate.utils.AuthManager

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hide action bar
        supportActionBar?.hide()

        // Wait for 2 seconds then go to main activity
        Handler(Looper.getMainLooper()).postDelayed({
            val auth = AuthManager(this)
            val next = if (auth.isLoggedIn()) MainActivity::class.java else LoginActivity::class.java
            startActivity(Intent(this, next))
            finish()
        }, 2000) // 2 seconds delay
    }
}