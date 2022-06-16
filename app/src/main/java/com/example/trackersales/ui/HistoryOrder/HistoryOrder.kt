package com.example.trackersales.ui.HistoryOrder

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.adapter.RecAdapterHistoryOrder

import com.example.trackersales.dataclass.Orders
import com.example.trackersales.dataclass.UserSales
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.*



class HistoryOrder : Fragment() {
    var historyOrderArrayList : ArrayList<Orders> = ArrayList()
    private lateinit var db: FirebaseFirestore

    private lateinit var recAdapterHistoryOrder: RecAdapterHistoryOrder
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_order, container, false)

        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)

        navBar.visibility=View.GONE

        fetchdata()

        initRecyclerViewHistoryOrder(view)

        return view
    }


    private fun initRecyclerViewHistoryOrder(view: View){
        val recylcerView = view.findViewById<RecyclerView>(R.id.recHistoryOrder)
        recylcerView.layoutManager= LinearLayoutManager(activity)
        recAdapterHistoryOrder= RecAdapterHistoryOrder(historyOrderArrayList)
        recylcerView.adapter=recAdapterHistoryOrder

    }


    private fun fetchdata(){
        historyOrderArrayList.clear()
        db = FirebaseFirestore.getInstance()
        db.collection("orders").
        addSnapshotListener(object : EventListener<QuerySnapshot> {
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

                        historyOrderArrayList.add(dc.document.toObject(Orders::class.java))
                    }
                }
                recAdapterHistoryOrder.notifyDataSetChanged()
            }
        })
    }

}