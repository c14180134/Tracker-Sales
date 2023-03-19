package com.example.trackersales

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.adapter.RecAdapterHistoryOrder
import com.example.trackersales.databinding.FragmentDetailCustomerBinding
import com.example.trackersales.databinding.FragmentDetailUserBinding
import com.example.trackersales.dataclass.Orders
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.*


class DetailCustomer : Fragment() {
    // TODO: Rename and change types of parameters
    private var customerid: String? = null
    private var namacustomer: String? = null
    private var noTelepon: String? = null
    private var Long: Double? = null
    private var Lat: Double? = null
    private var TotalPembelian: Long? = null
    private var totalpengeluaran: Long? = null
    private var tanggal: String? = null
    private var alamat: String? = null

    private lateinit var recAdapterHistoryOrder: RecAdapterHistoryOrder

    private var _binding: FragmentDetailCustomerBinding? = null

    private val binding get() = _binding!!

    private lateinit var db : FirebaseFirestore

    var historyOrderArrayList : ArrayList<Orders> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            customerid=it.getString("UID")
            namacustomer = it.getString("namaCustomer")
            noTelepon = it.getString("notelepon")
            Long = it.getDouble("long")
            Lat = it.getDouble("lat")
            TotalPembelian=it.getLong("totalpembelian")
            totalpengeluaran=it.getLong("totalpengeluaran")
            tanggal=it.getString("tanggal")
            alamat=it.getString("alamat")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailCustomerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        db = FirebaseFirestore.getInstance()
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.GONE
        binding.TotalPembeliantv.text= TotalPembelian.toString()
        binding.totalPengeluaranTv.text= totalpengeluaran.toString()
        binding.tvDetailNama.text=namacustomer.toString()
        binding.tvAlamat.text=alamat.toString()
        binding.tvNoTelpCustomer.text=noTelepon.toString()

        binding.btnBack.setOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.btnEditTarget.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("customerid",customerid)
            bundle.putString("notelepon",noTelepon)
            bundle.putString("namacustomer",namacustomer)
            bundle.putString("alamat",alamat)
            TotalPembelian?.let { it1 -> bundle.putLong("totalbeli", it1) }
            it.findNavController().navigate(R.id.editCustomer,bundle)
        }

        fetchDataCustomerOrder()

        initRecyclerViewHistoryOrder(root)
        Log.d("Lihatorder",historyOrderArrayList.toString())
        return root
    }

    private fun initRecyclerViewHistoryOrder(view: View){
        val recylcerView = view.findViewById<RecyclerView>(R.id.recycleVieworderCustomer)
        recylcerView.layoutManager= LinearLayoutManager(activity)
        recAdapterHistoryOrder= RecAdapterHistoryOrder(historyOrderArrayList)
        recylcerView.adapter=recAdapterHistoryOrder

    }


    fun fetchDataCustomerOrder(){
        historyOrderArrayList.clear()
        db = FirebaseFirestore.getInstance()
        db.collection("orders").whereEqualTo("customer_id",customerid).
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