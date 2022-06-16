package com.example.trackersales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.dataclass.UserCustomer

class RecAdapterCustomer(private val customerList:ArrayList<UserCustomer>):RecyclerView.Adapter<RecAdapterCustomer.RecCustomerViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecAdapterCustomer.RecCustomerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_halaman_customer,parent,false)
        return RecCustomerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecAdapterCustomer.RecCustomerViewHolder, position: Int) {
        val customer : UserCustomer = customerList[position]
        holder.namacustomer.text=customer.name
        holder.noTelp.text=customer.notelepon.toString()
        holder.alamat.text=customer.alamat
        holder.tanggalCustomer.text=customer.tanggal
    }

    class RecCustomerViewHolder (itemView: View) :RecyclerView.ViewHolder(itemView){
        val namacustomer : TextView = itemView.findViewById(R.id.tvNamaCustomer)
        val noTelp : TextView = itemView.findViewById(R.id.tvNoTelpCustomer)
        val alamat : TextView = itemView.findViewById(R.id.tvAlamatCustomer)
        val tanggalCustomer : TextView = itemView.findViewById(R.id.tvTanggalCustomer)
    }

    override fun getItemCount(): Int {
        return customerList.size
    }
}