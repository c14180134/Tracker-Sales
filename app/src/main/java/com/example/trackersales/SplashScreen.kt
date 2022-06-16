package com.example.trackersales

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.example.trackersales.ui.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SplashScreen : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        setContentView(R.layout.activity_splash_screen)
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if(user!=null){
            val intent = Intent(this,MainActivityBottomNav::class.java)

            Handler().postDelayed({
                startActivity(intent)
                this.finish()//cant go back to splash screen
            }, 1000)
        }else{
            val intent = Intent(this,LoginActivity::class.java)

            Handler().postDelayed({
                startActivity(intent)
                this.finish()
                }, 2000)
        }
    }
}