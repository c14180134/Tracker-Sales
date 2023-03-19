package com.example.trackersales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.trackersales.R
import com.example.trackersales.dataclass.UserCustomer
import com.example.trackersales.dataclass.UserSales

class AdapterListSalesDialog(val context: Context, val Sales:MutableList<UserSales>): BaseAdapter()  {
    override fun getCount(): Int {
        return Sales.size
    }

    override fun getItem(p0: Int): Any {
        return Sales.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = p1?: LayoutInflater.from(context).inflate(R.layout.list_item_customer,p2,false)
        val SalesName=view.findViewById<TextView>(R.id.customerName)
        val sales = Sales.get(p0)
        SalesName.text = sales.email
        return view
    }
}