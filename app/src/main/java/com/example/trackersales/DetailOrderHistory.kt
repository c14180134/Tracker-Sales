package com.example.trackersales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.adapter.AdapterItem
import com.example.trackersales.adapter.AdapterMainTask
import com.example.trackersales.databinding.FragmentDetailOrderHistoryBinding
import com.example.trackersales.databinding.FragmentHomeBinding
import com.example.trackersales.dataclass.Item
import com.example.trackersales.room.Location


class DetailOrderHistory : Fragment() {

    private var _binding: FragmentDetailOrderHistoryBinding? = null

    private val binding get() = _binding!!

    private var param1: MutableList<Item>? = null
    private var namaDetailCustomer : String? = null
    private var tanggalDetail:String? = null
    private var totalHarga:String? = null
    private lateinit var itemAdapter: AdapterItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelableArrayList("ListItem")
            namaDetailCustomer=it.getString("nama")
            totalHarga=it.getString("totalHarga")
            tanggalDetail=it.getString("tanggal")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailOrderHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initRecyclerViewTask(root)

        binding.tvNamaCustomerDetailOrder.text=namaDetailCustomer
        binding.tvTanggalDetailOrder.text=tanggalDetail
        binding.tvTotalHargaDetailOrder.text="Rp."+totalHarga



        binding.imageButton2.setOnClickListener {
            it.findNavController().navigateUp()
        }
        return root
    }

    private fun initRecyclerViewTask(view: View){
        val recylcerView = view.findViewById<RecyclerView>(R.id.list_itemHistory)
        recylcerView.layoutManager= LinearLayoutManager(activity)
        itemAdapter= AdapterItem(view,param1!!,true)
        recylcerView.adapter=itemAdapter

    }


}