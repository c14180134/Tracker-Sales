package com.example.trackersales.adapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.adapter.RecAdapterHistoryOrder.*
import com.example.trackersales.dataclass.Orders
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecAdapterHistoryOrder(private val OrderList: List<Orders>) : RecyclerView.Adapter<RecHistoryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecHistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_history,parent,false)
        return RecHistoryViewHolder(itemView)
    }

    override fun onBindViewHolder(
        holder: RecHistoryViewHolder,
        position: Int
    ) {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/M/yyyy")
        val x = OrderList.sortedByDescending { LocalDate.parse(it.tanggal, dateTimeFormatter) }
        val order = x[position]
//        if(position<OrderList.size-1){
//            if(order.tanggal==OrderList[position+1].tanggal){
//                holder.tanggalCustomer.visibility=View.GONE
//            }
//        }
        val bundle = Bundle()
        bundle.putString("uniqueCode",order.uniqueid)
        bundle.putString("tanggal",order.tanggal)
        bundle.putString("nama",order.nama_customer)
        bundle.putString("totalHarga",order.total_harga.toString())
        bundle.putParcelableArrayList("ListItem",order.item)
        Log.d("order",order.toString())
        holder.uniqueCode.text=order.uniqueid.toString()
        holder.namacustomer.text=order.nama_customer.toString()
        holder.tanggalCustomer.text=order.tanggal.toString()
        holder.totalHarga.text=order.total_harga.toString()
        holder.tvLihatDetail.setOnClickListener{
            it.findNavController().navigate(R.id.detailOrderHistory,bundle)
        }

    }

    override fun getItemCount(): Int {
        return OrderList.size
    }
    class RecHistoryViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val namacustomer : TextView = itemView.findViewById(R.id.tvNamaCustomerHistoryOrder)
        val totalHarga : TextView = itemView.findViewById(R.id.tvTotalHarga)
        val uniqueCode : TextView = itemView.findViewById(R.id.tvuniqCode)
        val tanggalCustomer : TextView = itemView.findViewById(R.id.tvTanggalHistoryOrder)
        val tvLihatDetail :TextView= itemView.findViewById(R.id.tvLihatDetailOrder)
    }
}