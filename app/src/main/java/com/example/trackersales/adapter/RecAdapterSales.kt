package com.example.trackersales.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.RecyclerView

import android.os.Bundle
import android.util.Log
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import com.example.trackersales.R
import com.example.trackersales.SalesMapFragment
import com.example.trackersales.dataclass.UserSales


class RecAdapterSales(private val salesList: ArrayList<UserSales>) :RecyclerView.Adapter<RecAdapterSales.RecSalesViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecSalesViewHolder {
       val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_sales,parent,false)
        return RecSalesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecSalesViewHolder, position: Int) {
        val sales : UserSales = salesList[position]
        holder.email.text = sales.email
        holder.noTelp.text = sales.notlp
        val bundle = Bundle()
        //membawa longitude dan latitude
        salesList[position].long?.let { it1 -> bundle.putLong("longitude", it1) }
        salesList[position].lat?.let { it1 -> bundle.putLong("latitude", it1) }
        salesList[position].UID?.let{it1->bundle.putString("UID",it1)}
        salesList[position].target?.let{it1->bundle.putLong("target",it1)}
        salesList[position].email?.let{it1->bundle.putString("email",it1)}
        salesList[position].currentprogress?.let{it1->bundle.putLong("currentprogress",it1)}
        salesList[position].tanggalprogress?.let{it1->bundle.putString("tanggalprogress",it1)}
        salesList[position].timeUpdate?.let{it1->bundle.putString("timeUpdate",it1)}
        salesList[position].todaysold?.let{it1->bundle.putInt("todaySold",it1)}
        salesList[position].notlp?.let{it1->bundle.putString("notlp",it1)}
        holder.btnLocation.setOnClickListener{
            Log.d("detailsales",salesList[position].toString())

//            val activity = it.context as AppCompatActivity
//            val salesMapFg= SalesMapFragment()
////            salesMapFg.arguments = bundle
////            activity.supportFragmentManager.beginTransaction()
////                .replace(R.id.nav_host_fragment_activity_main_bottom_nav,salesMapFg,"A")
////                .addToBackStack("A")
////                .commit()

            it.findNavController().navigate(R.id.salesMapFragment,bundle)
        }
        holder.cardSales.setOnClickListener {
            it.findNavController().navigate(R.id.detailSales,bundle)
        }

    }

    override fun getItemCount(): Int {
        return salesList.size
    }
//fungsi yang digunakan untuk mengganti textview sesuai dengan nama dan value dari data yang didapat
    class RecSalesViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val email :TextView = itemView.findViewById(R.id.tvEmail)
        val noTelp :TextView = itemView.findViewById(R.id.tvNoTelp)
        val btnLocation : Button = itemView.findViewById(R.id.btn_Location_Sales)
        val cardSales: CardView = itemView.findViewById(R.id.card_sales)
    }

}