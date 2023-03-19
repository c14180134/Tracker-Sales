package com.example.trackersales

import android.Manifest
import android.R.attr
import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_PICK
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.trackersales.room.Location
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        var  gallerybutton = view.findViewById<Button>(R.id.galleryButton)

        if(this.context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.CAMERA) } !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),Array(5){
                Manifest.permission.CAMERA
            },100)
        }
        if(this.context?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) } !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(requireActivity(),Array(5){
                Manifest.permission.READ_EXTERNAL_STORAGE
            },40)
        }
        if (imageVCheck.drawable==null){
            imageVCheck.visibility=View.GONE
        }
        gallerybutton.setOnClickListener {
            val photoPickerIntent = Intent(ACTION_PICK)
            photoPickerIntent.type = "image/*"
            imageVCheck.visibility=View.VISIBLE
            gallerybutton.visibility=View.GONE
            startActivityForResult(photoPickerIntent, 40)
        }
        imageVCheck.setOnClickListener {
            val photoPickerIntent = Intent(ACTION_PICK)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, 40)
        }
        takePhotoBtn.setOnClickListener {
            imageVCheck.visibility=View.VISIBLE
            gallerybutton.visibility=View.GONE
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
        if (resultCode !== RESULT_CANCELED) {
            if(requestCode==100&& data!==null){
                val extras = data?.getExtras()
                imageBitmap = extras?.get("data") as Bitmap
                imageVCheck.setImageBitmap(imageBitmap)
                dataPhoto=data
                Log.d("data", extras.toString())
            }
            if (requestCode==40){
                val filePath: Uri = data!!.getData()!!

                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, filePath)
                imageVCheck.setImageBitmap(bitmap)
                imageBitmap=bitmap

            }
        }

    }

    private fun storePhoto(bitmap:Bitmap,uniqueid:String){


        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val storage = FirebaseStorage.getInstance()
        val data = baos.toByteArray()
        val storageRef = storage.reference
        val uploadTask = storageRef.child("check-in/"+ judulEt.text.toString()+"/"+uniqueid).putBytes(data)

        uploadTask.addOnSuccessListener {

            Log.d("berhasil upload gambar","haha")
        }.addOnFailureListener{
            Log.d("gagal upload","hehe")
        }

    }
    private var locationCallback2: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            db = FirebaseFirestore.getInstance()
            super.onLocationResult(p0)
            if(p0.equals(null)!=true){
                longitude = p0.lastLocation.longitude
                latitude = p0.lastLocation.latitude
                storeOrder()
            }
        }
    }

    private fun fetchLocation() {
        val task = fusedLocationClient.lastLocation
        checkLocPermission()
            val locationRequest = LocationRequest.create()
            locationRequest.setSmallestDisplacement(20f)
            locationRequest.setInterval(20000)
            locationRequest.setFastestInterval(20000)
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            if (isLocationEnabled()) {
                fusedLocationClient.requestLocationUpdates(locationRequest,locationCallback2,
                    Looper.getMainLooper())
                Handler().postDelayed({
                    fusedLocationClient.removeLocationUpdates(locationCallback2)
                }, 1000)

//                if(it!=null){
//                    longitude=it.longitude
//                    latitude=it.latitude
//                    storeOrder()
//                }else{
//                    Toast.makeText(this.context,"Tolong Cek GPS Anda", Toast.LENGTH_SHORT).show()
//                }
            } else {
                Toast.makeText(this.context, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }


    }
    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
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
        if(imageVCheck.drawable!=null){
            if(judulEt.text.toString()!=""){
                Log.d("halo","halo1")
                val items =HashMap<String,Any>()
                items.put("judul", judulEt.text.toString())
                items.put("tanggal",currentDate)
                items.put("long",longitude)
                items.put("lat",latitude)
                items.put("catatan", catatanEt.text.toString())
                items.put("sales_id",uidsp?:"")
                val collection=db.collection("checkin").document()
                items.put("uniqueid",collection.id)
                collection.set(items).addOnSuccessListener {
                    storePhoto(imageBitmap,collection.id)
                    Toast.makeText(this.context,"Sukses Membuat Laporan", Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
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
            else{
                Toast.makeText(this.context,"Tolong buat judul untuk membuat laporan", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this.context,"Tolong ambil barang untuk membuat laporan", Toast.LENGTH_SHORT).show()
        }


    }



}