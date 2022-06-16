package com.example.trackersales.ui.login

import android.content.Intent

import android.os.Bundle
import android.os.Handler
import android.os.Looper

import androidx.appcompat.app.AppCompatActivity

import android.util.Log

import android.widget.Button
import android.widget.EditText
import android.widget.Toast


import com.example.trackersales.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore

import com.example.trackersales.MainActivityBottomNav

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        getSupportActionBar()?.hide()

        auth = Firebase.auth

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val loginbutton = findViewById<Button>(R.id.login)
        val etEmail: EditText = findViewById(R.id.username)
        val etPassword: EditText = findViewById(R.id.password)


        loginbutton.setOnClickListener{

            if(etEmail.text.toString()!=""&&etPassword.text.toString()!=""){
                auth.signInWithEmailAndPassword(etEmail.text.toString()+"@gmail.com", etPassword.text.toString())
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("masuk", "signInWithEmail:success")
                            val user = auth.currentUser
                            val intent = Intent(this,MainActivityBottomNav::class.java)
                            startActivity(intent)
                            this.finish()//user cant go back to login page after
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("tidak masuk", etPassword.text.toString())
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()

                        }
                    }
            }else{
                Toast.makeText(baseContext, "Harap isi password / username",
                    Toast.LENGTH_SHORT).show()
            }


        }

    }
    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            this.finish()
            super.onBackPressed()

            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back again to leave", Toast.LENGTH_SHORT).show()

        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){

        }
    }
}

