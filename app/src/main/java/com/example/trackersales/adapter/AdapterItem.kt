package com.example.trackersales.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.example.trackersales.R
import com.example.trackersales.dataclass.Item

class AdapterItem (val context: Context,val items:MutableList<Item>):BaseAdapter() {


    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(p0: Int): Any {
        return items.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = p1?:LayoutInflater.from(context).inflate(R.layout.list_item,p2,false)
        val itemName=view.findViewById<TextView>(R.id.itemName)
        val jumlahItem =view.findViewById<TextView>(R.id.txtJumlah)
        val hargaItem =view.findViewById<TextView>(R.id.txtPrice)
        val item=items.get(p0)
        itemName.text = item.nama
        jumlahItem.text=item.jumlah.toString()
        hargaItem.text=item.harga.toString()
        return view
    }

    fun addItem(item:Item){
        items.add(item)
        notifyDataSetChanged()
    }
}