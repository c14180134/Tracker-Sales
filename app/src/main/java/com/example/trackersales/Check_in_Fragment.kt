package com.example.trackersales

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*




class Check_in_Fragment : Fragment() {
    private lateinit var imageVCheck:ImageView
    private lateinit var takePhotoBtn:ImageButton
    private lateinit var checkInButton: Button
    private lateinit var judulEt: EditText
    private lateinit var catatanEt:EditText
    private lateinit var db : FirebaseFirestore
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private lateinit var imageBitmap:Bitmap

    private var longitude = 0.0
    private  var latitude = 0.0

    private lateinit var dataPhoto:Intent
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.GONE


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        val view = inflater.inflate(R.layout.fragment_check_in_, container, false)
        imageVCheck = view.findViewById(R.id.imageViewCheckin)
        takePhotoBtn=view.findViewById(R.id.imageButton)
        judulEt=view.findViewById(R.id.judulCheckin)
        catatanEt=view.findViewById(R.id.editTextTextMultiLine)
        checkInButton=view.findViewById(R.id.checkin_button)

        if(this.context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) } !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),Array(5){
                Manifest.permission.CAMERA
            },100)
        }

        imageVCheck.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,100)
        }
        takePhotoBtn.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,100)
        }

        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            it.findNavController().navigateUp()
        }

        checkInButton.setOnClickListener {
            fetchLocation()
        }

        return view
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==100){
            val extras = data?.getExtras()
            imageBitmap = extras?.get("data") as Bitmap
            imageVCheck.setImageBitmap(imageBitmap)
            dataPhoto=data
            Log.d("data", extras.toString())
        }
    }

    private fun storePhoto(bitmap:Bitmap){


        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val storage = FirebaseStorage.getInstance()
        val data = baos.toByteArray()
        val storageRef = storage.reference
        val uploadTask = storageRef.child("check-in/"+ judulEt.text.toString()).putBytes(data)

        uploadTask.addOnSuccessListener {

            Log.d("berhasil upload gambar","haha")
        }.addOnFailureListener{
            Log.d("gagal upload","hehe")
        }

    }

    private fun fetchLocation() {
        val task = fusedLocationClient.lastLocation
        checkLocPermission()
        task.addOnSuccessListener {

            if(it!=null){
                longitude=it.longitude
                latitude=it.latitude
                Handler().postDelayed({
                    storeOrder()
                }, 100)
            }
        }
    }
    private fun checkLocPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(this.requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this.requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    private fun storeOrder(){
        val simpleDate = SimpleDateFormat("dd/M/yyyy")
        val currentDate = simpleDate.format(Date())

        db = FirebaseFirestore.getInstance()
//
//        val user = FirebaseAuth.getInstance().currentUser
//        var uid =""
//        user?.let {
//            uid = user.uid
//        }
        val sharedPref = activity?.getSharedPreferences("LocActive", Context.MODE_PRIVATE)
        var uidsp= sharedPref?.getString("UID","")
        if(judulEt.text.toString()!=""){
            Log.d("halo","halo1")
                val items =HashMap<String,Any>()
                items.put("judul", judulEt.text.toString())
                items.put("tanggal",currentDate)
                items.put("long",longitude)
                items.put("lat",latitude)
                items.put("catatan", catatanEt.text.toString())
                items.put("sales_id",uidsp?:"")
                db.collection("checkin").document().set(items).addOnSuccessListener {
//                    storePhoto(imageBitmap)
                    Toast.makeText(this.context,"success Create Order", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this.context,"it", Toast.LENGTH_SHORT).show()
                }

                var query= db.collection("users").whereEqualTo("uid",uidsp).get()
                query.addOnSuccessListener {
                    val items =HashMap<String,Any>()
                    items.put("long",longitude)
                    items.put("lat",latitude)
                    for(document in it){
                        db.collection("users").document(document.id).set(items, SetOptions.merge())
                    }
                }

        }

    }



}