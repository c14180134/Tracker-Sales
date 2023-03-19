package com.example.trackersales.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.trackersales.R
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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val TOPIC = "/topics/myTopic2"

class LocationServiceCoba:Service(){

    val TAG="Halosd"

    private lateinit var sensorManager:SensorManager

    private lateinit var db: FirebaseFirestore

    var x = 0
    var y = 0

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
//                val longitude = p0.lastLocation.longitude.toString()
//                val latitude = p0.lastLocation.latitude.toString()
////                Log.d("Location Update3",longitude+" , "+latitude)
               var query= db.collection("users").whereEqualTo("uid",uid).get()
                query.addOnSuccessListener {
                    Log.d("speed",speed.toString())
                    val items =HashMap<String,Any>()
                    var time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                    items.put("timeUpdate",time)
                    items.put("speed",speed.toString())
                    items.put("long",p0.lastLocation.longitude)
                    items.put("lat",p0.lastLocation.latitude)
                    val distance = FloatArray(2)
                    for(document in it){
                        if(document["targetlong"] != null && document["targetlat"] != null){
                            android.location.Location.distanceBetween(p0.lastLocation.latitude,p0.lastLocation.longitude, document["targetlat"].toString().toDouble(),document["targetlong"].toString().toDouble(),distance)
                            if(distance[0]>4000){
                                Log.d("distanceoutside",distance[0].toString())
                                var querynotif = db.collection("users").whereEqualTo("admin",true).get()
                                querynotif.addOnSuccessListener {
                                    for (documentadmin in it){
                                        if(document["tokenFCM"]!=null){
                                            Log.d("distance",documentadmin["tokenFCM"].toString())

                                            PushNotifikasi(
                                                Notifikasi("Out Of Radius","Sales"+document["email"].toString()+" keluar dari radius yang ditentukan"), documentadmin["tokenFCM"].toString()
                                            ).also {
                                                sendNotification(it)
                                            }
                                        }
                                    }
                                }
                            }
                            Log.d("distance",distance[0].toString())
                        }
                        if(speed>30){
                            PushNotifikasi(
                                Notifikasi("driver is too fast",document["email"].toString()+" melebihi kecepatan 80 km/h"), com.example.trackersales.TOPIC
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

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }


    @SuppressLint("MissingPermission")
    fun startLocationService(){
//        setUpSensorStuff()
        Log.d("xxx",x.toString()+"adsfadfs"+y.toString())
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
        locationRequest.setSmallestDisplacement(20f)
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


    }

    fun stopLocationServiceinWorkManager(){

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
                stopSelf()
            }.addOnFailureListener {
                stopSelf()
            }
            xd.nukeTable()
        }

    }


    fun stopLocationService(){
//        sensorManager.unregisterListener(this)
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
                }else if(action.equals(Constants.ACTION_STOP_LOCATION_SERVICE_IN_WM)){
                    stopLocationServiceinWorkManager()
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
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

//    private fun setUpSensorStuff(){
//        sensorManager= getSystemService(SENSOR_SERVICE)as SensorManager
//        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also {
//            sensorManager.registerListener(this,it,
//                SensorManager.SENSOR_DELAY_GAME,
//                SensorManager.SENSOR_DELAY_GAME)
//        }
//
//    }

//    @SuppressLint("MissingPermission")
//    override fun onSensorChanged(p0: SensorEvent?) {
//        var sudah_nyala=false
//        if(p0?.sensor?.type== Sensor.TYPE_ACCELEROMETER){
//            val sides = p0.values[0]
//            val upDown= p0.values[1]
//
//            x = sides.toInt()
//            y = upDown.toInt()
//
//            if(x ==0 && y==0){
//                LocationServices.getFusedLocationProviderClient(applicationContext).removeLocationUpdates(locationCallback)
//                LocationServices.getFusedLocationProviderClient(applicationContext).removeLocationUpdates(locationCallback2)
//                sudah_nyala=false
//            }else{
//                if(sudah_nyala==false){
//                    sudah_nyala = true
//                    val locationRequest = LocationRequest.create()
//                    locationRequest.setSmallestDisplacement(20f)
//                    locationRequest.setInterval(20000)
//                    locationRequest.setFastestInterval(20000)
//                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//
//                    val locationRequestLokalDB = LocationRequest.create()
//                    locationRequestLokalDB.setSmallestDisplacement(20f)
//                    locationRequestLokalDB.setInterval(10000)
//                    locationRequestLokalDB.setFastestInterval(10000)
//                    locationRequestLokalDB.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//
//                    LocationServices.getFusedLocationProviderClient(this)
//                        .requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper())
//                    LocationServices.getFusedLocationProviderClient(this)
//                        .requestLocationUpdates(locationRequestLokalDB,locationCallback2, Looper.getMainLooper())
//                }
//
//            }
//
//        }
//    }

//    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
//        return
//    }

}