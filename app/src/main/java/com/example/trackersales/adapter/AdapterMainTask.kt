package com.example.trackersales.adapter

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.dataclass.TanggalTask
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.HashMap

class AdapterMainTask(private val context: Context, private val tanggalList:ArrayList<TanggalTask>): RecyclerView.Adapter<AdapterMainTask.RecMainTaskViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecMainTaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task_sales,parent,false)
        return RecMainTaskViewHolder(itemView)
    }
    private lateinit var db : FirebaseFirestore
    class RecMainTaskViewHolder (itemView: View) :RecyclerView.ViewHolder(itemView){
        val tanggalTask : TextView = itemView.findViewById(R.id.tvTanggalTask)
        val recItemTask:RecyclerView = itemView.findViewById(R.id.rvIsiTask)
        val buttondone: Button = itemView.findViewById(R.id.btnDone)
    }

    override fun onBindViewHolder(holder: RecMainTaskViewHolder, position: Int) {
        db = FirebaseFirestore.getInstance()
        var taskref= db.collection("task")
        val tanggalTask : TanggalTask = tanggalList[position]
        holder.tanggalTask.text=tanggalTask.tanggal
        if(tanggalTask.done==true){
            holder.buttondone.setText("Cancel")
            holder.buttondone.setBackgroundColor(Color.parseColor("#d41336"))
        }else{
            holder.buttondone.setText("Done")
            holder.buttondone.setBackgroundColor(Color.parseColor("#FF01579B"))
        }
        holder.recItemTask.layoutManager=LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        holder.recItemTask.adapter= tanggalTask.listTask?.let { AdapterIsiTask(it,tanggalList[position],false) }

        holder.tanggalTask.setOnClickListener{
            if(holder.recItemTask.visibility==View.GONE) {
                holder.recItemTask.visibility = View.VISIBLE
            }else{
                holder.recItemTask.visibility = View.GONE

            }
        }
        holder.buttondone.setOnClickListener {

            if(tanggalTask.done==true){
                Log.d("dick",holder.buttondone.text.toString())
                var query= taskref.whereEqualTo("tanggal",tanggalTask.tanggal).whereEqualTo("uid",tanggalTask.uid.toString()).get()
                query.addOnSuccessListener {
                    Log.d("dick",holder.buttondone.text.toString())
                    val items = HashMap<String,Any>()
                    items.put("done",false)
                    for(document in it){
                        db.collection("task").document(document.id).set(items, SetOptions.merge())
                    }
                }
                tanggalTask.done=false
                holder.buttondone.setText("Done")
                holder.buttondone.setBackgroundColor(Color.parseColor("#FF01579B"))
                notifyItemChanged(position)
            }else{
                var query= taskref.whereEqualTo("tanggal",tanggalTask.tanggal).whereEqualTo("uid",tanggalTask.uid.toString()).get()
                query.addOnSuccessListener {
                    Log.d("dd",tanggalTask.uid.toString())
                    val items = HashMap<String,Any>()
                    items.put("done",true)
                    for(document in it){
                        db.collection("task").document(document.id).set(items, SetOptions.merge())
                    }

                }
                tanggalTask.done=true
                holder.buttondone.setText("Cancel")
                holder.buttondone.setBackgroundColor(Color.parseColor("#d41336"))
                notifyItemChanged(position)
            }

        }
    }

    override fun getItemCount(): Int {
        return tanggalList.count()
    }
}