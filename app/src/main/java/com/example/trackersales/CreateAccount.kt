package com.example.trackersales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.trackersales.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class CreateAccount : Fragment() {
    private lateinit var passwordEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var noTeleponEditText: EditText

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_create_account, container, false)
        auth = Firebase.auth
        db= FirebaseFirestore.getInstance()
        passwordEditText=view.findViewById(R.id.pwEt)
        emailEditText=view.findViewById(R.id.emailEt)
        noTeleponEditText=view.findViewById(R.id.noTelpEt)

        view.findViewById<Button>(R.id.btnRegister).setOnClickListener {

        }

        view.findViewById<Button>(R.id.btnBack).setOnClickListener {
            it.findNavController().navigateUp()
        }

        return view
    }

    private fun registerUser(){
        var pwRegister = passwordEditText.text.toString()
        var emailRegister = emailEditText.text.toString()
        var noTeleponRegister = noTeleponEditText.text.toString()

        if(emailRegister.isEmpty()){
            emailEditText.setError("Email Is Required")
            emailEditText.requestFocus()
        }

        if(pwRegister.isEmpty()){
            passwordEditText.setError("Password Is Required")
            passwordEditText.requestFocus()
        }

        if(pwRegister.length < 6){
            passwordEditText.setError("Password Is too Short (it should be 6 characters or more)")
            passwordEditText.requestFocus()
        }
        val simpleDate = SimpleDateFormat("dd/M/yyyy")
        val currentDate = simpleDate.format(Date())
        auth.createUserWithEmailAndPassword(emailRegister,pwRegister)
            .addOnCompleteListener {
                val items =HashMap<String,Any>()
                items.put("email",emailRegister)
                items.put("notlp",noTeleponRegister)
                items.put("admin",false)
                items.put("lat",0)
                items.put("long",0)
                items.put("tanggalbergabung",currentDate)
                items.put("tanggalprogress",currentDate)
                items.put("target",0)
                items.put("timeUpdate","00:00")
                items.put("tanggalbergabung",currentDate)
                items.put("todaysold",0)
                var userid=auth.currentUser?.uid
                items.put("uid",userid.toString())
                val collection=db.collection("user").document()
                collection.set(items).addOnSuccessListener {
                }.addOnFailureListener {
                    Toast.makeText(this.context,it.toString(), Toast.LENGTH_SHORT).show()
                }
            }

    }

}