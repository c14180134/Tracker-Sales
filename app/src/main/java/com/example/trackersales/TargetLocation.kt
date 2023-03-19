package com.example.trackersales

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.HashMap


class TargetLocation : Fragment() {

    private lateinit var mMap: GoogleMap

    var currentMarker:Marker?=null
    var currentCircle:Circle?=null
    private var targetLongitude:Double?=null
    private var targetLatitude:Double?=null
    private var uids: String? = null
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uids = it.getString("UID")
        }
    }
    private val callback = OnMapReadyCallback { googleMap ->
        mMap=googleMap

        mMap.setOnMapClickListener(object :GoogleMap.OnMapClickListener{
            override fun onMapClick(p0: LatLng?) {
                if (currentMarker!=null){
                    currentMarker?.remove()
                    currentCircle?.remove()
                }
                targetLongitude=p0!!.longitude
                targetLatitude=p0!!.latitude
                drawMarker(p0!!)
            }

        })

        mMap.setOnMarkerDragListener(object :GoogleMap.OnMarkerDragListener{
            override fun onMarkerDragStart(p0: Marker?) {

            }

            override fun onMarkerDrag(p0: Marker?) {

            }

            override fun onMarkerDragEnd(p0: Marker?) {

            }

        })

    }
    override fun onStart() {
        super.onStart()
        var uidRecycler=arguments?.get("UID")
        fetchLocationUser(uidRecycler.toString())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        val vieww=inflater.inflate(R.layout.fragment_target_location, container, false)
        vieww.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            it.findNavController().navigateUp()
        }

        vieww.findViewById<Button>(R.id.SaveButton).setOnClickListener {
            var query= db.collection("users").whereEqualTo("uid",uids).get()
            query.addOnSuccessListener {
                val items = HashMap<String,Any>()
                items.put("targetlong", targetLongitude!!)
                items.put("targetlat",targetLatitude!!)
                for(document in it){
                    db.collection("users").document(document.id).set(items, SetOptions.merge()).addOnSuccessListener {
                        Toast.makeText(this.context,"Sukses mengganti lokasi tujuan target", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this.context,"Ada yang salah", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


        return vieww
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapViewHistory) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }

    fun drawMarker(point: LatLng){

        drawCircle(point)

        val markerOptions = MarkerOptions().position(point!!).title(point.toString()).draggable(true)


        mMap.animateCamera(CameraUpdateFactory.newLatLng(point))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 13f))
        currentMarker=mMap.addMarker(markerOptions)

    }

    private fun drawCircle(point: LatLng) {
        // Instantiating CircleOptions to draw a circle around the marker
        val circleOptions = CircleOptions()
        // Specifying the center of the circle
        circleOptions.center(point)
        // Radius of the circle
        circleOptions.radius(3183.0)
        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK)
        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000)
        // Border width of the circle
        circleOptions.strokeWidth(2f)
        // Adding the circle to the GoogleMap
        currentCircle = mMap.addCircle(circleOptions)
    }

    fun fetchLocationUser(uid:String){
        db.collection("users")
            .whereEqualTo("uid",uid)
            .addSnapshotListener { value, error ->
            error?.let {
                Toast.makeText(this.requireContext(),it.message, Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }
            value?.let {
                for(document in it){

                        mMap.clear()
                    if (document["targetlong"]!=null&&document["targetlat"]!=null){
                        targetLatitude=document["targetlat"].toString().toDouble()
                        targetLongitude=document["targetlong"].toString().toDouble()
                    }else{
                        var longitude =document["long"].toString().toDouble()
                        var latitude =document["lat"].toString().toDouble()
                        targetLatitude=latitude
                        targetLongitude=longitude
                    }
                    val latLng = LatLng(targetLatitude!!, targetLongitude!!)
                    drawMarker(latLng)
                }
            }
        }
    }

}