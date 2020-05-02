package com.example.mobphotoedit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler

    class SplashActivity : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_splash)

            val intent = Intent(this, MainActivity::class.java)
            Handler().postDelayed({
                startActivity(intent)
                finish()
            }, 3000)

        }
    }