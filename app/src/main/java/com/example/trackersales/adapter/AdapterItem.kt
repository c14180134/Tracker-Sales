package com.example.trackersales.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.dataclass.Item

class AdapterItem (val context: View,val items:MutableList<Item>,val History:Boolean): RecyclerView.Adapter<AdapterItem.RecAdapterItemViewHolder>()  {
    class RecAdapterItemViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val DelButton:ImageButton=itemView.findViewById(R.id.deleteButton)
        val itemName: TextView= itemView.findViewById(R.id.itemName)
        val jumlahItem: TextView =itemView.findViewById(R.id.txtJumlah)
        val hargaItem: TextView =itemView.findViewById(R.id.txtPrice)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecAdapterItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)

        return AdapterItem.RecAdapterItemViewHolder(itemView)
    }

    fun addItem(item:Item){
        items.add(item)
        notifyDataSetChanged()
    }



    override fun getItemCount(): Int {
        return items.count()
    }

    override fun onBindViewHolder(holder: RecAdapterItemViewHolder, position: Int) {
        if (History==true){
            holder.DelButton.visibility=View.GONE
        }

        var item = items[position]
        holder.itemName.text=item.nama
        holder.hargaItem.text=item.harga.toString()
        holder.jumlahItem.text=item.jumlah.toString()
        holder.DelButton.setOnClickListener {

            items.removeAt(position)
            notifyItemRemoved(position)
            var x =  0
            for(i in items){
                x= (x+i.harga!!).toInt()
            }
            context.findViewById<TextView>(R.id.tvTotalHarga).text=x.toString()
            notifyItemRangeChanged(position,items.size)
        }
    }
}