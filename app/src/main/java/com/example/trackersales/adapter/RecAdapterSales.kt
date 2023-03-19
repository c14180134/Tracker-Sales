package com.example.trackersales.adapter

import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import androidx.recyclerview.widget.RecyclerView

import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
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
        if(sales.admin==false&&sales.speed!=null){
            holder.speed.text = sales.speed+"km/jam"
        }else{
            holder.speed.visibility = View.GONE
        }
        if(sales.admin==false&&sales.tanggalprogress!=null){
            holder.lastLog.text = "last login: " + sales.tanggalprogress
        }else{
            holder.lastLog.visibility = View.GONE
        }
        holder.targetbar.max= sales.target?.toInt() ?:0
        ObjectAnimator.ofInt(holder.targetbar,"progress", sales.currentprogress?.toInt() ?: 0)
            .setDuration(2000)
            .start()

        Log.d("sales",sales.toString() )
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

            it.findNavController().navigate(R.id.salesMapFragment,bundle)
        }

        if(holder.IS_ADMIN==true){
            holder.cardSales.setOnClickListener {
                it.findNavController().navigate(R.id.detailSales,bundle)
            }
        }

    }

    override fun getItemCount(): Int {
        return salesList.size
    }
//fungsi yang digunakan untuk mengganti textview sesuai dengan nama dan value dari data yang didapat
    class RecSalesViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val preferences = itemView.getContext().getSharedPreferences("LocActive", Context.MODE_PRIVATE)
        var IS_ADMIN= preferences?.getBoolean("IS_ADMIN",false)!!
        val email :TextView = itemView.findViewById(R.id.tvEmail)
        val noTelp :TextView = itemView.findViewById(R.id.tvNoTelp)
        val btnLocation : Button = itemView.findViewById(R.id.btn_Location_Sales)
        val cardSales: CardView = itemView.findViewById(R.id.card_sales)
        val targetbar:ProgressBar=itemView.findViewById(R.id.progressBar3)
        val speed:TextView=itemView.findViewById(R.id.speedmeterTv)
        val lastLog:TextView=itemView.findViewById(R.id.lastLoginTv)
    }

}