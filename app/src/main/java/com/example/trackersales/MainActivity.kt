package com.example.trackersales

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.trackersales.ui.sales.ListSales
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val user = Firebase.auth.currentUser
        user?.let {
            val name = user.displayName
            val email = user.email

            val uid = user.uid
            Log.d("nama" ,uid)

        }
        val cbBtn = findViewById<Button>(R.id.btnkesales)
        cbBtn.setOnClickListener{
            val intent = Intent(this,MainActivityBottomNav::class.java)
            startActivity(intent)
        }

    }
}