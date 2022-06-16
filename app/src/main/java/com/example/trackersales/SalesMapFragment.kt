package com.example.trackersales

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.trackersales.dataclass.UserSales
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.firestore.FirebaseFirestore


class SalesMapFragment : Fragment() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var permissionID = 52
    lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mMap: GoogleMap
    private lateinit var db: FirebaseFirestore


    private var uidRecylcer: String? = null
    private var email: String? = null
    private var tanggalProgress: String? = null
    private var targetRecycler: Long? = null
    private var currentProgress: Long? = null
    private var todaySold: Int? = null
    private var updateTime: String? = null
    private var notlp: String? = null

    private lateinit var listSales:ArrayList<UserSales>

    var  MY_PERMISSIONS_REQUEST_CALL_PHONE = 1000

    private lateinit var tvUpdateTime : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email=it.getString("email")
            uidRecylcer = it.getString("UID")
            targetRecycler = it.getLong("target")
            currentProgress= it.getLong("currentprogress")
            tanggalProgress=it.getString("tanggalprogress")
            todaySold=it.getInt("todaySold")
            updateTime=it.getString("timeUpdate")
            notlp=it.getString("notlp")
        }
    }

    private val callback = OnMapReadyCallback { googleMap ->
        mMap=googleMap

    }

    override fun onStart() {
        super.onStart()
        var uidRecycler=arguments?.get("UID")
        fetchLocationUser(uidRecycler.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listSales= arrayListOf()
        val viewLayout =inflater.inflate(R.layout.fragment_sales_map, container, false)
        db = FirebaseFirestore.getInstance()
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.GONE
        fusedLocationClient=LocationServices.getFusedLocationProviderClient(this.requireActivity())




        bottomSheetBehavior = BottomSheetBehavior.from(viewLayout.findViewById(R.id.bottomSheet))

        bottomSheetBehavior.isHideable=false

        viewLayout.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            it.findNavController().navigateUp()
        }



        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheetBehavior.halfExpandedRatio= 0.5F
                }
                else{
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {

                }
            }
        })






        val bottomlayout = viewLayout.findViewById<View>(R.id.bottomSheet)
        tvUpdateTime=bottomlayout.findViewById(R.id.tvLastLocationUpdate)
        bottomlayout.findViewById<TextView>(R.id.tvNamaSales).text=email
        bottomlayout.findViewById<TextView>(R.id.tvPerDaySale).text=todaySold.toString()
        bottomlayout.findViewById<TextView>(R.id.tvLastLocationUpdate).text=updateTime
        bottomlayout.findViewById<ImageView>(R.id.btnPhone).setOnClickListener{
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + notlp))
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(Manifest.permission.CALL_PHONE),
                    MY_PERMISSIONS_REQUEST_CALL_PHONE)
            }else{
                startActivity(intent)
            }

        }

        return viewLayout
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        mapFragment?.getMapAsync(callback)

    }


    fun fetchLocationUser(uid:String){
        listSales.clear()
        db.collection("users").addSnapshotListener { value, error ->
            error?.let {
                Toast.makeText(this.requireContext(),it.message,Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            value?.let {
                for(document in it){
                    listSales.add(document.toObject(UserSales::class.java))
                    if(document["uid"].toString()==uid){

                        mMap.clear()
                        var longitude =document["long"].toString().toDouble()
                        var latitude =document["lat"].toString().toDouble()
                        val latLng = LatLng(latitude, longitude)
                        val markerOptions = MarkerOptions().position(latLng)
                        updateTime= document["timeUpdate"].toString()
                        todaySold=document["todaysold"].toString().toInt()
                        mMap.addMarker(markerOptions)
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                        tvUpdateTime.text=updateTime
                    }
                }
            }
        }

    }



}