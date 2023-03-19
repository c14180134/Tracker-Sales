package com.example.trackersales

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.navigation.findNavController
import com.example.trackersales.dataclass.HistoryLokasi
import com.example.trackersales.room.Location
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.LatLng

class HistoryMap : Fragment() {
    private var param1: ArrayList<Location>? = null

    private lateinit var mMap: GoogleMap


    private val callback = OnMapReadyCallback { googleMap ->
        mMap=googleMap
        if(param1?.size!! >0){
            for(d in param1!!){
                val latLng = d.Latitude?.let { d.Longitude?.let { it1 ->
                    com.google.android.gms.maps.model.LatLng(it,
                        it1
                    )
                } }
                val markerOptions = latLng?.let { MarkerOptions().position(it) }
                mMap.addMarker(markerOptions)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            }
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelableArrayList("Lokasi")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_history_map, container, false)
        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            it.findNavController().navigateUp()
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.mapViewHistory) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)



    }

}