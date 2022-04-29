package com.example.trackersales

import android.app.PendingIntent.getActivity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController

import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.ui.notifications.NotificationsFragment
import androidx.core.content.ContextCompat.startActivity

import android.content.Intent
import androidx.core.content.ContextCompat
import android.os.Bundle
import android.util.Log


class RecAdapterSales(private val salesList: ArrayList<UserSales>) :RecyclerView.Adapter<RecAdapterSales.RecSalesViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecAdapterSales.RecSalesViewHolder {
       val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_sales,parent,false)
        return RecSalesViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecAdapterSales.RecSalesViewHolder, position: Int) {
        val sales : UserSales = salesList[position]
        holder.email.text = sales.email
        holder.noTelp.text = sales.notlp
        holder.btnLocation.setOnClickListener{
            Log.d("detailsales",salesList[position].toString())
            val bundle = Bundle()
            salesList[position].long?.let { it1 -> bundle.putLong("longitude", it1) }
            salesList[position].lat?.let { it1 -> bundle.putLong("latitude", it1) }
            val activity = it.context as AppCompatActivity
            val salesMapFg= SalesMapFragment()
            salesMapFg.arguments = bundle
            activity.supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main_bottom_nav,salesMapFg,"A")
                .addToBackStack("A")
                .commit()

//            it.findNavController().navigate(R.id.salesMapFragment)
        }
    }

    override fun getItemCount(): Int {
        return salesList.size
    }

    public class RecSalesViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val email :TextView = itemView.findViewById(R.id.tvEmail)
        val noTelp :TextView = itemView.findViewById(R.id.tvNoTelp)
        val btnLocation : Button = itemView.findViewById(R.id.btn_Location_Sales)

    }

}