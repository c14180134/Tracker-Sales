package com.example.trackersales.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.LocationResult

class LocationService:BroadcastReceiver() {

    override fun onReceive(p0: Context?, p1: Intent?) {
        if(p1 != null){
            val action=p1.action.toString()
            if(ACTION_PROCESS_UPDATE.equals(action)){
                var result = LocationResult.extractResult(p1)
                if(result.equals(null)!=true){
                    var location =result.lastLocation
                    Log.d("coba lokasi jalan","Location Result:Longitude"+location.longitude.toString()+" Latitude"+location.latitude.toString())
                    Toast.makeText(p0,"Location Result:Longitude"+location.longitude.toString()+" Latitude"+location.latitude.toString(), Toast.LENGTH_SHORT).show()

                }
                else{

                }
            }
        }
    }

    companion object {
        const val ACTION_PROCESS_UPDATE="edmt.dev.googlelocationbackground.UPDATE_LOCATION"
    }
}