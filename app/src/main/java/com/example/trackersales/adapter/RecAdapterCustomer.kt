package com.example.trackersales.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
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
        holder.cardCustomer.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("UID",customer.customerid)
            bundle.putString("namaCustomer",customer.name)
            bundle.putString("alamat",customer.alamat)
            customer.alamatLat?.let { it1 -> bundle.putDouble("lang", it1) }
            customer.alamatLong?.let { it1 -> bundle.putDouble("long", it1) }
            bundle.putString("notelepon",customer.notelepon.toString())
            bundle.putString("tanggal",customer.tanggal)
            customer.seluruhpengeluaran?.let { it1 -> bundle.putLong("totalpengeluaran", it1) }
            customer.totalbeli?.let { it1 -> bundle.putLong("totalpembelian", it1) }
            it.findNavController().navigate(R.id.detailCustomer,bundle)
        }
    }

    class RecCustomerViewHolder (itemView: View) :RecyclerView.ViewHolder(itemView){
        val namacustomer : TextView = itemView.findViewById(R.id.tvNamaCustomer)
        val noTelp : TextView = itemView.findViewById(R.id.tvNoTelpCustomer)
        val alamat : TextView = itemView.findViewById(R.id.tvAlamatCustomer)
        val tanggalCustomer : TextView = itemView.findViewById(R.id.tvTanggalCustomer)
        val cardCustomer: CardView= itemView.findViewById(R.id.holderCustomer)
    }

    override fun getItemCount(): Int {
        return customerList.size
    }
}