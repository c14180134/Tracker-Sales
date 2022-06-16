package com.example.trackersales.adapter

import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R

class AdapterIsiTask(private val taskList:ArrayList<String>,val edit:Boolean): RecyclerView.Adapter<AdapterIsiTask.RecAdapterIsiTaskViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecAdapterIsiTaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_isi_task,parent,false)
        return RecAdapterIsiTaskViewHolder(itemView)
    }

    class RecAdapterIsiTaskViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val namaTask : TextView = itemView.findViewById(R.id.tvTask)
        val CheckButton:ImageButton = itemView.findViewById(R.id.checkButton)
        val DelButton:ImageButton=itemView.findViewById(R.id.deleteButton)
    }

    override fun onBindViewHolder(
        holder: RecAdapterIsiTaskViewHolder,
        position: Int
    ) {
        val Task = taskList[position]
        holder.namaTask.text=Task
        if(edit){
            holder.CheckButton.visibility=View.GONE
        }else{
            holder.DelButton.visibility=View.GONE
        }
    }

    override fun getItemCount(): Int {
        return taskList.count()
    }
}