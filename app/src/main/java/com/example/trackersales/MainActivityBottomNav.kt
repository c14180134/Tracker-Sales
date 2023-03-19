package com.example.trackersales

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.trackersales.databinding.ActivityMainBottomNavBinding
import com.example.trackersales.dataclass.UserSales
import com.example.trackersales.service.Constants
import com.example.trackersales.service.LocationServiceCoba
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.HashMap

class MainActivityBottomNav : AppCompatActivity() {

    private lateinit var binding: ActivityMainBottomNavBinding

    private lateinit var db: FirebaseFirestore
    private var permissionID = 52
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSupportActionBar()?.hide()
        auth = Firebase.auth
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("Error", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }
                val user = auth.currentUser
                var uid =""
                user?.let {
                    uid = user.uid
                }
                val token = task.result
                Log.d("token",task.result)
                db = FirebaseFirestore.getInstance()
                db.collection("users").whereEqualTo("uid",uid).get()
                    .addOnCompleteListener {
                        for(dc : DocumentChange in it.result.documentChanges!!){
                            if(dc.type == DocumentChange.Type.ADDED){
                                val items = HashMap<String, Any>()
                                items.put("tokenFCM",task.result)
                               db.collection("users").document(dc.document.id).set(items, SetOptions.merge())
                            }
                        }
                    }

            })
        binding = ActivityMainBottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main_bottom_nav) as NavHostFragment
        val navController = navHostFragment.navController




        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )



        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }



    private fun CheckPermission():Boolean{
        if (
            ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    private fun RequestPermission(){
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,android.Manifest.permission.ACCESS_FINE_LOCATION),permissionID
        )
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun isServiceRunning(): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (LocationServiceCoba::class.java.toString() == service.service.className) {
                if(service.foreground){
                    return true
                }

            }
        }
        return false
    }
    fun startLocationService(){
        if(!isServiceRunning()){
            var intent=Intent(applicationContext,LocationServiceCoba::class.java)
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE)
            startService(intent)
            Toast.makeText(this,"location Service Started",Toast.LENGTH_SHORT).show()
        }
    }

    fun stopLocationService(){
            var intent=Intent(applicationContext,LocationServiceCoba::class.java)
            intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE)
            startService(intent)
            Toast.makeText(this,"location Service Stoped",Toast.LENGTH_SHORT).show()

    }

    fun stopLocationServiceinWM(){
        var intent=Intent(applicationContext,LocationServiceCoba::class.java)
        intent.setAction(Constants.ACTION_STOP_LOCATION_SERVICE_IN_WM)
        startService(intent)
        Toast.makeText(this,"location Service Stoped",Toast.LENGTH_SHORT).show()

    }


    private var doubleBackToExitPressedOnce = false
    override fun onBackPressed() {
        val x = findNavController(R.id.nav_host_fragment_activity_main_bottom_nav)
        Log.d("counting",x.currentDestination.toString())
        val count = supportFragmentManager.backStackEntryCount
        Log.d("counting",count.toString())
        if (x.currentDestination?.label=="Home") {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }
            this.doubleBackToExitPressedOnce = true
            Toast.makeText(this, "Press back again to leave", Toast.LENGTH_SHORT).show()

        } else {
            x.navigateUp()
        }





        Handler(Looper.getMainLooper()).postDelayed(Runnable { doubleBackToExitPressedOnce = false }, 2000)
    }

    override fun onStart() {
        if(CheckPermission()){
            if(!isLocationEnabled()){
                AlertDialog.Builder(this).setMessage("GPS Not Enabled")
                    .setPositiveButton("Open Setting",DialogInterface.OnClickListener { dialogInterface, i ->
                        startActivity(Intent(Settings.ACTION_SETTINGS))
                    })

            }
        }else{
            RequestPermission()
        }
        super.onStart()
//        val user = FirebaseAuth.getInstance().currentUser
//        var uid =""
//        user?.let {
//            uid = user.uid
//        }
//        db= FirebaseFirestore.getInstance()
//        db.collection("users").
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
//                        if(pengecualian.UID.toString()==uid){
//                            val sharedPref = getSharedPreferences("LocActive", Context.MODE_PRIVATE)
//                            val editors=sharedPref?.edit()
//                            editors?.putString("UID",uid)
//                            pengecualian.admin?.let { editors?.putBoolean("IS_ADMIN", it) }
//                            editors?.commit()
//                        }
//
//                    }
//                }
//            }
//        })

    }


}