package com.example.trackersales.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.dataclass.TanggalTask

class AdapterMainTask(private val context: Context, private val tanggalList:ArrayList<TanggalTask>): RecyclerView.Adapter<AdapterMainTask.RecMainTaskViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecMainTaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task_sales,parent,false)
        return RecMainTaskViewHolder(itemView)
    }

    class RecMainTaskViewHolder (itemView: View) :RecyclerView.ViewHolder(itemView){
        val tanggalTask : TextView = itemView.findViewById(R.id.tvTanggalTask)
        val recItemTask:RecyclerView = itemView.findViewById(R.id.rvIsiTask)
    }

    override fun onBindViewHolder(holder: RecMainTaskViewHolder, position: Int) {
        val tanggalTask : TanggalTask = tanggalList[position]
        holder.tanggalTask.text=tanggalTask.tanggal
        holder.recItemTask.layoutManager=LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        holder.recItemTask.adapter= tanggalTask.listTask?.let { AdapterIsiTask(it,false) }
    }

    override fun getItemCount(): Int {
        return tanggalList.count()
    }
}