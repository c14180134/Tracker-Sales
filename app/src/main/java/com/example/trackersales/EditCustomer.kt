package com.example.trackersales

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.trackersales.databinding.FragmentDetailCustomerBinding
import com.example.trackersales.databinding.FragmentEditCustomerBinding
import com.example.trackersales.databinding.FragmentEditProfileUserBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage


class EditCustomer : Fragment() {

    private var _binding: FragmentEditCustomerBinding? = null

    private val binding get() = _binding!!

    private lateinit var db : FirebaseFirestore

    private var customerid: String? = null
    private var namacustomer: String? = null
    private var notelepon: String? = null
    private var totalbeli: Long? = null
    private var alamat: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            customerid = it.getString("customerid")
            namacustomer = it.getString("namacustomer")
            notelepon = it.getString("notelepon")
            totalbeli = it.getLong("totalbeli")
            alamat = it.getString("alamat")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditCustomerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        db = FirebaseFirestore.getInstance()



        binding.alamatEt.setText(alamat)
        binding.noTelpEt.setText(notelepon)
        binding.TotalPembelianet.setText(totalbeli.toString())
        binding.namaCustomerEt.setText(namacustomer)

        binding.btnEdit.setOnClickListener {
            showdialog()
        }

        return root
    }

    fun showdialog(){
        AlertDialog.Builder(this.context).setMessage("Change Profile Info?")
            .setPositiveButton("Ok") { dialog, _ ->
                if (binding.namaCustomerEt.text.toString() != "" && binding.noTelpEt.text.toString() != ""
                    && binding.TotalPembelianet.text.toString() != "" && binding.alamatEt.text.toString() != "") {
                    val nama = binding.namaCustomerEt.text.toString()
                    val noTelepon = binding.noTelpEt.text.toString()
                    val totalbeli = binding.TotalPembelianet.text.toString()
                    val alamat = binding.alamatEt.text.toString()
                    var customer = db.collection("customer")
                    var query = customer.whereEqualTo("customerid", customerid).get()
                    query.addOnSuccessListener {
                        var items = HashMap<String, Any>()
                        items.put("name", nama)
                        items.put("notelepon",noTelepon)
                        items.put("totalbeli",totalbeli.toLong())
                        items.put("alamat",alamat)
                        Log.d("hal",items.toString())
                        for (document in it) {

                            db.collection("customer").document(document.id)
                                .set(items, SetOptions.merge())
                        }
                    }
                    Toast.makeText(this.context, "Berhasil mengubah info Customer", Toast.LENGTH_SHORT)
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