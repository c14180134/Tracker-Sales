package com.example.trackersales

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.trackersales.databinding.FragmentDetailUserBinding
import com.example.trackersales.databinding.FragmentEditProfileUserBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class EditProfileUser : Fragment() {

    private var _binding: FragmentEditProfileUserBinding? = null

    private val binding get() = _binding!!

    private lateinit var db : FirebaseFirestore

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString("notelepon")
            param2 = it.getString("namasales")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileUserBinding.inflate(inflater, container, false)
        val root: View = binding.root
        db = FirebaseFirestore.getInstance()


        binding.namasalesEt.setText(param2)
        binding.noTelpEt.setText(param1)

        binding.btnBack.setOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.btnEdit.setOnClickListener {
            showdialog()
        }
        return root
    }

    fun showdialog(){
        val user = FirebaseAuth.getInstance().currentUser
        var uid =""
        user?.let {
            uid = user.uid
        }
        AlertDialog.Builder(this.context).setMessage("Change Profile Info?")
            .setPositiveButton("Ok") { dialog, _ ->
                if (binding.namasalesEt.text.toString() != "") {
                    val nama = binding.namasalesEt.text.toString()
                    val noTelepon = binding.noTelpEt.text.toString()
                    var sales = db.collection("users")
                    var query = sales.whereEqualTo("uid", uid).get()
                    query.addOnSuccessListener {
                        var items = HashMap<String, Any>()
                        items.put("email", nama)
                        items.put("notlp",noTelepon)
                        for (document in it) {

                            db.collection("users").document(document.id)
                                .set(items, SetOptions.merge())
                        }
                    }
                    Toast.makeText(this.context, "Berhasil mengubah Profile", Toast.LENGTH_SHORT)
                        .show()
                    findNavController().navigate(R.id.navigation_home)
                    dialog.dismiss()
                } else {
                    Toast.makeText(
                        this.context,
                        "Tolong isi data dengan lengkap",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

}