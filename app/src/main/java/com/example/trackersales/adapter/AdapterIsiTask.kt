package com.example.trackersales.adapter

import android.content.ContentValues.TAG
import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.dataclass.IsiTask
import com.example.trackersales.dataclass.TanggalTask
import com.example.trackersales.dataclass.UserSales
import com.google.firebase.firestore.*

class AdapterIsiTask(private val taskList:MutableList<IsiTask>,val tasking: TanggalTask?=null,val edit:Boolean): RecyclerView.Adapter<AdapterIsiTask.RecAdapterIsiTaskViewHolder>()  {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecAdapterIsiTaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_isi_task,parent,false)
        return RecAdapterIsiTaskViewHolder(itemView)
    }
    private lateinit var db : FirebaseFirestore

    class RecAdapterIsiTaskViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val namaTask : TextView = itemView.findViewById(R.id.tvTask)
        val CheckButton:ImageButton = itemView.findViewById(R.id.checkButton)
        val DelButton:ImageButton=itemView.findViewById(R.id.deleteButton)
    }

    override fun onBindViewHolder(
        holder: RecAdapterIsiTaskViewHolder,
        position: Int
    ) {
        db = FirebaseFirestore.getInstance()

        val Task = taskList[position]
        holder.namaTask.text=Task.isi
        holder.DelButton.setOnClickListener {
            taskList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,taskList.size)
        }
        if(edit){

            holder.CheckButton.visibility=View.GONE
        }else{
            holder.CheckButton.visibility=View.GONE
            holder.DelButton.visibility=View.GONE
//            if(Task.checked==true) {
//                holder.CheckButton.setBackgroundResource(R.drawable.ic_baseline_check_box_24)
//            }
//            holder.CheckButton.setOnClickListener {
//                var query= db.collection("task")
//                query.whereEqualTo("tanggal", tasking?.tanggal).whereEqualTo("uid", tasking?.uid).whereArrayContains("listTask",Task)
//                query.get().addOnCompleteListener{ itTask->
//                    itTask.apply {
//                        if (itTask.isSuccessful) {
//                            for (document in result) {
//
//                                val docIdRef = query.document(document.id)

//                                val listTaskfs = document.toObject(TanggalTask::class.java).listTask
//                                var isitaskd= IsiTask()
//                                isitaskd.isi=Task.isi
//                                if(Task.checked==false){
//                                    isitaskd.checked=true
//                                }else{
//                                    isitaskd.checked=false
//                                }
//                                Log.d("masukd",isitaskd.toString())
//                                listTaskfs?.let {
//                                    listTaskfs.remove(Task)
//                                    listTaskfs.add(isitaskd)
//                                    docIdRef.set(mutableMapOf("listTask" to listTaskfs), SetOptions.merge()).addOnCompleteListener{ setTask ->
//                                        if (setTask.isSuccessful) {
//                                            Log.d(TAG, "Update complete.")
//                                        } else {
//                                            setTask.exception?.message?.let {
//                                                Log.e(TAG, "gk bisa masuk")
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        } else {
//                            itTask.exception?.message?.let {
//                                Log.e(TAG, it)
//                            }
//                        }
//                    }
//                }
//                if(Task.checked==true){
//                    holder.CheckButton.setBackgroundResource(R.drawable.ic_baseline_check_box_24)
//                }else{
//                    holder.CheckButton.setBackgroundResource(R.drawable.ic_baseline_check_box_24)
//                }
//            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.count()
    }
}