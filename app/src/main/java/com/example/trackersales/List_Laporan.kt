package com.example.trackersales

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.adapter.AdapterLaporan
import com.example.trackersales.adapter.RecAdapterHistoryOrder
import com.example.trackersales.adapter.RecAdapterHistoryPerjalanan
import com.example.trackersales.databinding.FragmentDetailCustomerBinding
import com.example.trackersales.databinding.FragmentDetailLaporanBinding
import com.example.trackersales.databinding.FragmentListLaporanBinding
import com.example.trackersales.dataclass.Laporan_Check_in
import com.example.trackersales.dataclass.Orders
import com.google.firebase.firestore.*


class List_Laporan : Fragment() {
    private lateinit var recAdapterLaporan: AdapterLaporan

    private var _binding: FragmentListLaporanBinding? = null

    private val binding get() = _binding!!

    private lateinit var db : FirebaseFirestore

    var listLaporan : ArrayList<Laporan_Check_in> = ArrayList()

    private var uid: String? = null
    private var email: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString("UID")
            email = it.getString("email")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListLaporanBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.btnBack.setOnClickListener {
            it.findNavController().navigateUp()
        }
        fetchDataCustomerOrder()
        initRecyclerViewCustomer(root)
        return root
    }

    private fun initRecyclerViewCustomer(view: View){
        val recylcerView = view.findViewById<RecyclerView>(R.id.recLaporan)
        recylcerView.layoutManager= LinearLayoutManager(activity)
        recAdapterLaporan= AdapterLaporan(listLaporan)
        recylcerView.adapter=recAdapterLaporan

    }

    fun fetchDataCustomerOrder(){
        listLaporan.clear()
        db = FirebaseFirestore.getInstance()
        db.collection("checkin").whereEqualTo("sales_id",uid).
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

                        listLaporan.add(dc.document.toObject(Laporan_Check_in::class.java))
                    }
                }
                recAdapterLaporan.notifyDataSetChanged()
            }
        })
    }



}