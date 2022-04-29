package com.example.trackersales.ui.sales

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.RecAdapterSales
import com.example.trackersales.UserSales
import com.google.firebase.firestore.*

class ListSales : AppCompatActivity(){

    private lateinit var recyclerView: RecyclerView
    private lateinit var salesArrayList :ArrayList<UserSales>
    private lateinit var recAdapterSales: RecAdapterSales
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_sales)

        recyclerView = findViewById(R.id.recyclerView_sales)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        salesArrayList = arrayListOf()

        recAdapterSales = RecAdapterSales(salesArrayList)

        recyclerView.adapter = recAdapterSales

        eventChangeListener()

    }

    private fun eventChangeListener(){
        db = FirebaseFirestore.getInstance()
        db.collection("users").
                addSnapshotListener(object : EventListener<QuerySnapshot>{
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ) {
                        if(error !=null){
                            Log.e("Firestore Error",error.message.toString())
                            return
                        }
                        for(dc : DocumentChange in value?.documentChanges!!){
                            if(dc.type == DocumentChange.Type.ADDED){

                                salesArrayList.add(dc.document.toObject(UserSales::class.java))
                            }
                        }
                        recAdapterSales.notifyDataSetChanged()
                    }
                })
    }
}