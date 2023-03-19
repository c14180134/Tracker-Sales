package com.example.trackersales

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.trackersales.databinding.FragmentDetailUserBinding
import com.example.trackersales.databinding.FragmentHomeBinding
import com.example.trackersales.dataclass.Orders
import com.example.trackersales.dataclass.UserSales
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class Detail_User : Fragment() {

    private var _binding: FragmentDetailUserBinding? = null

    private val binding get() = _binding!!

    private lateinit var db : FirebaseFirestore


    var namasales =""
    var noTeleponSales=""
    var tanggalbergabung = ""
    var targetSales:Long=0
    var totalNetWorth=0



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailUserBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.GONE
        db = FirebaseFirestore.getInstance()
        fetchUserData()


        binding.btnBack.setOnClickListener {
            it.findNavController().navigateUp()
        }

//        val user = FirebaseAuth.getInstance().currentUser
//        var namasales =""
//
//        user?.let {
//            namasales = user.email.toString()
//        }

        startLoading()


//        Handler().postDelayed({
//            binding.tvNamaSales.text=namasales
//            binding.tvTanggalJoin.text=tanggalbergabung
//            binding.tvTarget.text=targetSales.toString()
//            binding.tvNoTelpSales.text=noTeleponSales
//            binding.tvNetWorth.text="Rp."+totalNetWorth.toString()
//                              }, 400)

        binding.btnEditProfile.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("notelepon",noTeleponSales)
            bundle.putString("namasales",namasales)
            it.findNavController().navigate(R.id.editProfileUser,bundle)
        }



        return root
    }
    fun startLoading(){
        var loadingDialog: AlertDialog
        val dialogLoading=layoutInflater.inflate(R.layout.custom_loading_dialog,null)
        val builder = AlertDialog.Builder(this.context)
        builder.setView(dialogLoading)
        builder.setCancelable(false)
        loadingDialog=builder.create()
        loadingDialog.show()
        loadingDialog.window?.setBackgroundDrawableResource(R.drawable.camera_checkin_pic)
        loadingDialog.window?.setLayout(600,500)
        Handler().postDelayed({
            loadingDialog.dismiss()
        }, 500)
    }

    fun fetchUserData(){
        val sharedPref = activity?.getSharedPreferences("LocActive", Context.MODE_PRIVATE)
        var uidsp= sharedPref?.getString("UID","")
        db.collection("users").whereEqualTo("uid",uidsp).get()
            .addOnCompleteListener {
                for(dc : DocumentChange in it.result.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        namasales=dc.document["email"].toString()
                        noTeleponSales=dc.document["notlp"].toString()
                        targetSales= dc.document["target"].toString().toLong()
                        tanggalbergabung =dc.document["tanggalbergabung"].toString()
                    }
                }
                if(it.isComplete){
                    binding.tvNamaSales.text=namasales
                    binding.tvTanggalJoin.text=tanggalbergabung
                    binding.tvTarget.text=targetSales.toString()
                    binding.tvNoTelpSales.text=noTeleponSales
                    binding.tvNetWorth.text="Rp."+totalNetWorth.toString()
                }
            }
//        addSnapshotListener(object : EventListener<QuerySnapshot> {
//            override fun onEvent(
//                value: QuerySnapshot?,
//                error: FirebaseFirestoreException?
//            ) {
//                if(error !=null){
//                    Log.e("Firestore Error",error.message.toString())
//                    return
//                }
//                for(dc : DocumentChange in value?.documentChanges!!){
//                    if(dc.type == DocumentChange.Type.ADDED){
//                        val pengecualian = dc.document.toObject(UserSales::class.java)
//                        if(pengecualian.UID.toString()==uidsp){
//                            namasales=pengecualian.email.toString()
//                            noTeleponSales=pengecualian.notlp.toString()
//                            targetSales= pengecualian.target?:0
//                            tanggalbergabung =pengecualian.tanggalbergabung.toString()
//                        }
//
//                    }
//                }
//            }
//        })

        val otherdata = db.collection("orders")
        otherdata.addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if(error !=null){
                    Log.e("Firestore Error",error.message.toString())
                    return
                }
                for(dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        val pengecualian = dc.document.toObject(Orders::class.java)
                        if(pengecualian.sales_id.toString()==uidsp){
                            totalNetWorth+= pengecualian.total_harga?.toInt() ?: 0
                        }
                    }
                }
            }
        })
    }

}