package com.example.trackersales.ui.login

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.trackersales.databinding.ActivityLoginBinding

import com.example.trackersales.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import com.example.trackersales.MainActivity
import com.example.trackersales.MainActivityBottomNav

import com.google.android.gms.tasks.OnFailureListener

import com.google.firebase.firestore.DocumentReference

import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth


class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {


        auth = Firebase.auth

        val db = FirebaseFirestore.getInstance()
        val user: MutableMap<String, Any> = HashMap()
//        user["first"] = "Ada"
//        user["last"] = "Lovelace"
//        user["born"] = 1815
//        Log.d("HALLO", user.toString())
//        db.collection("users")
//            .add(user)
//            .addOnSuccessListener { documentReference ->
//                Log.d(
//                    "berhasil",
//                    "DocumentSnapshot added with ID: " + documentReference.id
//                )
//            }
//            .addOnFailureListener { e -> Log.w("helo", "Error adding document", e) }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val loginbutton = findViewById<Button>(R.id.login)
        val etEmail: EditText = findViewById(R.id.username)
        val etPassword: EditText = findViewById(R.id.password)


        loginbutton.setOnClickListener{
            auth.signInWithEmailAndPassword(etEmail.text.toString()+"@gmail.com", etPassword.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("masuk", "signInWithEmail:success")
                        val user = auth.currentUser
                        val intent = Intent(this,MainActivityBottomNav::class.java)
                        startActivity(intent)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.d("tidak masuk", etPassword.text.toString())
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()

                    }
                }

        }

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){

        }
    }
}

