package com.example.trackersales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.trackersales.R
import com.example.trackersales.dataclass.UserCustomer

class AdapterListCustomerDialog (val context: Context,val Customers:MutableList<UserCustomer>):BaseAdapter() {
    override fun getCount(): Int {
        return Customers.size
    }

    override fun getItem(p0: Int): Any {
        return Customers.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = p1?: LayoutInflater.from(context).inflate(R.layout.list_item_customer,p2,false)
        val customerName=view.findViewById<TextView>(R.id.customerName)
        val customer = Customers.get(p0)
        customerName.text = customer.name
        return view
    }
}