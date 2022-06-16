package com.example.trackersales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.trackersales.adapter.AdapterItem
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

        itemAdapter = AdapterItem(this.requireContext(), param1!!)

        binding.tvNamaCustomerDetailOrder.text=namaDetailCustomer
        binding.tvTanggalDetailOrder.text=tanggalDetail
        binding.tvTotalHargaDetailOrder.text="Rp."+totalHarga

        binding.listItemHistory.adapter=itemAdapter

        // Inflate the layout for this fragment
        return root
    }


}