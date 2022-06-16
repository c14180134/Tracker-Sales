package com.example.trackersales.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.trackersales.R
import com.example.trackersales.room.Location
import com.example.trackersales.room.LocationDB
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class LocationServiceCoba:Service() {

    private lateinit var db: FirebaseFirestore

    val lokaldb by lazy { LocationDB(this) }

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
            db = FirebaseFirestore.getInstance()
            val user = FirebaseAuth.getInstance().currentUser
            var uid =""
            user?.let {
                uid = user.uid
            }
            super.onLocationResult(p0)
            if(p0.equals(null)!=true){
                val longitude = p0.lastLocation.longitude.toString()
                val latitude = p0.lastLocation.latitude.toString()
                Log.d("Location Update3",longitude+" , "+latitude)
               var query= db.collection("users").whereEqualTo("uid",uid).get()
                query.addOnSuccessListener {
                    val items =HashMap<String,Any>()
                    var time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                    items.put("timeUpdate",time)
                    items.put("long",p0.lastLocation.longitude)
                    items.put("lat",p0.lastLocation.latitude)
                    for(document in it){
                        db.collection("users").document(document.id).set(items, SetOptions.merge())
                    }
                }
            }
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    @SuppressLint("MissingPermission")
    private fun startLocationService(){
        val channelId ="location_notification_channel"
        val notificationManager =  getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val resultIntent=Intent()
        val pendingIntent = PendingIntent.getActivities(
            applicationContext,0, arrayOf(resultIntent),PendingIntent.FLAG_UPDATE_CURRENT)
        val builder = NotificationCompat.Builder (
            applicationContext,
            channelId
        )
        builder.setSmallIcon(R.mipmap.ic_launcher )
        builder.setContentTitle ("Location Service")
        builder.setDefaults(NotificationCompat.DEFAULT_ALL)
        builder.setContentText(" Running")
        builder.setContentIntent(pendingIntent)
        builder.setAutoCancel(false)

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            if(notificationManager!=null&&notificationManager.getNotificationChannel(channelId)==null){
                val notificationChannel = NotificationChannel(channelId,"LocationService",NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.description="Used by Location Service"
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        val locationRequest = LocationRequest.create()
        locationRequest.setSmallestDisplacement(50f)
        locationRequest.setInterval(20000)
        locationRequest.setFastestInterval(20000)
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        val locationRequestLokalDB = LocationRequest.create()
        locationRequestLokalDB.setSmallestDisplacement(20f)
        locationRequestLokalDB.setInterval(10000)
        locationRequestLokalDB.setFastestInterval(10000)
        locationRequestLokalDB.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
        LocationServices.getFusedLocationProviderClient(this)
            .requestLocationUpdates(locationRequestLokalDB,locationCallback2, Looper.getMainLooper())
        startForeground(Constants.LOCATION_SERVICE_ID,builder.build())
        //dpt digunakan untuk membatasi penggunaan service dri user

//        Handler().postDelayed({
//            stopLocationService()
//        }, 10000)
    }


    fun stopLocationService(){

        db = FirebaseFirestore.getInstance()
        val simpleDate = SimpleDateFormat("dd/M/yyyy")
        val currentDate = simpleDate.format(Date())
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback)
        LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback2)
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
                stopForeground(true)
                stopSelf()
            }.addOnFailureListener {
                stopForeground(true)
                stopSelf()
            }
            xd.nukeTable()
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent!=null){
            val action = intent.action
            if(action!=null){
                if(action.equals(Constants.ACTION_START_LOCATION_SERVICE)) {
                    startLocationService()
                }else if(action.equals(Constants.ACTION_STOP_LOCATION_SERVICE)){
                    stopLocationService()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

}