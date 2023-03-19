package com.example.trackersales.workManager

import android.annotation.SuppressLint
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.trackersales.TOPIC
import com.example.trackersales.dataclass.Notifikasi
import com.example.trackersales.dataclass.PushNotifikasi
import com.example.trackersales.forfirebasecloudmessaging.RetrofitInstance
import com.example.trackersales.room.Location
import com.example.trackersales.room.LocationDB
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.logging.SimpleFormatter

open class UpdateLocationWorker(context: Context, params:WorkerParameters):Worker(context,params) {
    val TAG="Halosd"

    private lateinit var db: FirebaseFirestore

    val lokaldb by lazy { LocationDB(this.applicationContext) }

    private var locationCallback2: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(p0: LocationResult) {
            db = FirebaseFirestore.getInstance()
            super.onLocationResult(p0)
            if(p0.equals(null)!=true){
                val longitude = p0.lastLocation.longitude
                val latitude = p0.lastLocation.latitude

                CoroutineScope(Dispatchers.IO).launch {
                    lokaldb.LocationDao().addLoc(
                        Location(0,longitude,latitude)
                    )
                }
            }
        }
    }


    private var locationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(p0: LocationResult) {
            FirebaseMessaging.getInstance().subscribeToTopic(com.example.trackersales.TOPIC)
            db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser
            var uid =""
            user?.let {
                uid = user.uid
            }
            super.onLocationResult(p0)
            if(p0.equals(null)!=true){
                var speed = p0.lastLocation.speed
                val longitude = p0.lastLocation.longitude.toString()
                val latitude = p0.lastLocation.latitude.toString()
                Log.d("Location Update3",longitude+" , "+latitude)
                var query= db.collection("users").whereEqualTo("uid",uid).get()
                query.addOnSuccessListener {
                    Log.d("speed",speed.toString())
                    val items = HashMap<String,Any>()
                    var time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                    items.put("timeUpdate",time)
                    items.put("speed",speed.toString())
                    items.put("long",p0.lastLocation.longitude)
                    items.put("lat",p0.lastLocation.latitude)
                    for(document in it){
                        if(speed>50){
                            PushNotifikasi(
                                Notifikasi("driver is too fast",document["email"].toString()+" melebihi kecepatan 80 km/h"), TOPIC
                            ).also {
                                sendNotification(it)
                            }
                        }
                        db.collection("users").document(document.id).set(items, SetOptions.merge())
                    }
                }
            }
        }
    }
    private fun sendNotification(notification: PushNotifikasi) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    @SuppressLint("MissingPermission")
    override fun doWork(): Result {
        try{
            val locationRequest = LocationRequest.create()
            locationRequest.setSmallestDisplacement(20f)
            locationRequest.setInterval(20000)
            locationRequest.setFastestInterval(20000)
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

            val locationRequestLokalDB = LocationRequest.create()
            locationRequestLokalDB.setSmallestDisplacement(20f)
            locationRequestLokalDB.setInterval(10000)
            locationRequestLokalDB.setFastestInterval(10000)
            locationRequestLokalDB.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

            LocationServices.getFusedLocationProviderClient(this.applicationContext)
                .requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
            LocationServices.getFusedLocationProviderClient(this.applicationContext)
                .requestLocationUpdates(locationRequestLokalDB,locationCallback2, Looper.getMainLooper())

            val time = SimpleDateFormat("dd/MM/yyyy hh:mm:ss")
            val currentDate = time.format(Date())
            Log.d("coba Worker","Completed $currentDate")

            return Result.success()
        }catch(e:Exception) {
            return Result.failure()
        }
    }

    fun stopLocationService(){

        db = FirebaseFirestore.getInstance()
        val simpleDate = SimpleDateFormat("dd/M/yyyy")
        val currentDate = simpleDate.format(Date())
        LocationServices.getFusedLocationProviderClient(this.applicationContext).removeLocationUpdates(locationCallback)
        LocationServices.getFusedLocationProviderClient(this.applicationContext).removeLocationUpdates(locationCallback2)
        val items = HashMap<String, Any>()
        CoroutineScope(Dispatchers.IO).launch {
            val xd=lokaldb.LocationDao()
            items.put("Location",xd.getLocation())
            Log.d("mama",xd.getLocation().toString())
            val user = FirebaseAuth.getInstance().currentUser
            var uid =""
            user?.let {
                uid = user.uid
            }
            items.put("uid",uid)
            items.put("tanggal",currentDate)

            db.collection("History").document().set(items).addOnSuccessListener {
            }.addOnFailureListener {

            }
            xd.nukeTable()
        }

    }
}