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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.adapter.RecAdapterCustomer
import com.example.trackersales.adapter.RecAdapterHistoryPerjalanan
import com.example.trackersales.dataclass.HistoryLokasi
import com.example.trackersales.dataclass.UserCustomer
import com.example.trackersales.dataclass.UserSales
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import java.util.ArrayList
import java.util.HashMap
import java.util.zip.Inflater

class ListHistoryPerjalanan : Fragment() {
    private lateinit var db: FirebaseFirestore
    var historyArrayList : ArrayList<HistoryLokasi> = ArrayList()
    private lateinit var recAdapterHistoryPerjalanan: RecAdapterHistoryPerjalanan
    private var uidRecylcer: String? = null
    private var email: String? = null
    private var targetRecycler: Long? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email=it.getString("email")
            uidRecylcer = it.getString("UID")
            targetRecycler = it.getLong("target")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_list_history_perjalanan, container, false)
        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            it.findNavController().navigateUp()
        }
        eventChangeListenerCustomer()
        initRecyclerViewCustomer(view)

        return view
    }

    private fun initRecyclerViewCustomer(view: View){
        val recylcerView = view.findViewById<RecyclerView>(R.id.recHistoryPerjalanan)
        recylcerView.layoutManager= LinearLayoutManager(activity)
        Log.d("histod",historyArrayList.toString())
        recAdapterHistoryPerjalanan= RecAdapterHistoryPerjalanan(historyArrayList)
        recylcerView.adapter=recAdapterHistoryPerjalanan

    }

    private fun eventChangeListenerCustomer() {
        historyArrayList.clear()
        db = FirebaseFirestore.getInstance()
        var query= db.collection("History").whereEqualTo("uid",uidRecylcer)
        query.addSnapshotListener(object : EventListener<QuerySnapshot> {
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
                            historyArrayList.add(dc.document.toObject(HistoryLokasi::class.java))
                    }
                }
                recAdapterHistoryPerjalanan.notifyDataSetChanged()
            }
        })
    }
}