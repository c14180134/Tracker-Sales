package com.example.trackersales.ui.HistoryOrder

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.adapter.RecAdapterHistoryOrder

import com.example.trackersales.dataclass.Orders
import com.example.trackersales.dataclass.UserSales
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class HistoryOrder : Fragment() {
    var historyOrderArrayList : ArrayList<Orders> = ArrayList()
    var historyOrderArrayListTemp : ArrayList<Orders> = ArrayList()
    private lateinit var db: FirebaseFirestore


    private lateinit var recAdapterHistoryOrder: RecAdapterHistoryOrder

    private var uid: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyOrderArrayList.clear()
        arguments?.let {
            uid = it.getString("UID")
            email = it.getString("email")
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_history_order, container, false)

        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)

        navBar.visibility=View.GONE
        if (isAdmin()){
            historyOrderArrayList.clear()
            historyOrderArrayListTemp.clear()
            if(uid!=null){
                fetchdataSales()
            }else{
                fetchdataAdmin()
            }
        }else{
            historyOrderArrayList.clear()
            historyOrderArrayListTemp.clear()
            if(uid!=null){
                fetchdataSales()
            }else{
                fetchdata()
            }
        }
        val searchView:SearchView = view.findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newTexts: String?): Boolean {
                historyOrderArrayList.clear()
                val searchText = newTexts!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    historyOrderArrayListTemp.forEach {
                        if(it.nama_customer!!.toLowerCase(Locale.getDefault()).contains(searchText) ||it.tanggal!!.toLowerCase(Locale.getDefault()).contains(searchText) ){
                            historyOrderArrayList.add(it)
                        }
                    }
                    recAdapterHistoryOrder.notifyDataSetChanged()
                }else{
                    historyOrderArrayList.addAll(historyOrderArrayListTemp)
                    recAdapterHistoryOrder.notifyDataSetChanged()
                }
                return false
            }

        })


        initRecyclerViewHistoryOrder(view)

        return view
    }


    private fun initRecyclerViewHistoryOrder(view: View){

        val recylcerView = view.findViewById<RecyclerView>(R.id.recHistoryOrder)
        recylcerView.layoutManager= LinearLayoutManager(activity)
        recAdapterHistoryOrder= RecAdapterHistoryOrder(historyOrderArrayList)
        recylcerView.adapter=recAdapterHistoryOrder

    }

    fun isAdmin(): Boolean {
        val sharedPref = activity?.getSharedPreferences("LocActive", Context.MODE_PRIVATE)
        var IS_ADMIN= sharedPref?.getBoolean("IS_ADMIN",false)!!
        if(IS_ADMIN!=true){
            return false
        }
        return true
    }

    private fun fetchdataSales(){

        historyOrderArrayList.clear()
        db = FirebaseFirestore.getInstance()
        db.collection("orders").whereEqualTo("sales_id",uid).
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
                        historyOrderArrayListTemp.add(dc.document.toObject(Orders::class.java))
                        historyOrderArrayList.add(dc.document.toObject(Orders::class.java))
                    }
                }
                val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/M/yyyy")
                historyOrderArrayList.sortedByDescending { LocalDate.parse(it.tanggal, dateTimeFormatter) }
//                historyOrderArrayList.sortByDescending { it.tanggal }
                recAdapterHistoryOrder.notifyDataSetChanged()
            }
        })
    }

    private fun fetchdataAdmin(){
        historyOrderArrayList.clear()

        db = FirebaseFirestore.getInstance()
        db.collection("orders").whereNotEqualTo("sales_id","").
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
                        historyOrderArrayListTemp.add(dc.document.toObject(Orders::class.java))
                        historyOrderArrayList.add(dc.document.toObject(Orders::class.java))
                    }
                }
                val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/M/yyyy")
                historyOrderArrayList.sortedByDescending { LocalDate.parse(it.tanggal, dateTimeFormatter) }

                recAdapterHistoryOrder.notifyDataSetChanged()
            }
        })
    }

    private fun fetchdata(){
        historyOrderArrayList.clear()
        Log.d("halo",historyOrderArrayList.toString())
        db = FirebaseFirestore.getInstance()
        val user = FirebaseAuth.getInstance().currentUser
        db.collection("orders").whereEqualTo("sales_id",user?.uid).
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
                        historyOrderArrayListTemp.add(dc.document.toObject(Orders::class.java))
                        historyOrderArrayList.add(dc.document.toObject(Orders::class.java))
                    }
                }
                val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/M/yyyy")
                val x = historyOrderArrayList.sortedByDescending { LocalDate.parse(it.tanggal, dateTimeFormatter) }
                recAdapterHistoryOrder.notifyDataSetChanged()
            }
        })
    }

}