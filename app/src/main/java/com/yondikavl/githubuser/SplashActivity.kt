package com.yondikavl.githubuser

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val delayMillis = 1200L
        val handler = Handler()

        val startMainActivity = Runnable {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }

        handler.postDelayed(startMainActivity, delayMillis)
    }
}
