package com.example.mobphotoedit

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_last.*
import kotlin.repeat

class LastActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_last)

        repeat.setOnClickListener {
            switchActivity()
        }
    }

    private fun switchActivity(){
        val i = Intent(LastActivity@this, MainActivity::class.java)
        startActivity(i)
    }

    override fun onStart() {
        super.onStart()
        stars.onStart()
    }

    override fun onStop() {
        stars.onStop()
        super.onStop()
    }
}
