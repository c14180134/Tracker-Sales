package com.example.trackersales.ui.task

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.adapter.AdapterIsiTask
import com.example.trackersales.databinding.FragmentAddTaskBinding
import com.example.trackersales.dataclass.IsiTask
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class AddTask : Fragment(),DateSelected {
    val listTask = ArrayList<IsiTask>()
    private var uid: String? = null
    private lateinit var db : FirebaseFirestore
    private var _binding: FragmentAddTaskBinding? = null

    private val binding get() = _binding!!
    private lateinit var recAdapterIsiTask: AdapterIsiTask
    private var Date:Date?=null
    lateinit var etDate :EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString("UID")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = FirebaseFirestore.getInstance()
        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root
        etDate=binding.etDate

        binding.saveTask.setOnClickListener {
            TambahTaskFS()
        }
        binding.btnPlusTask.setOnClickListener {
            addInfo()
        }
        initRecyclerViewTask(root)
        binding.btnDate.setOnClickListener {
            showDate()
        }

        return root
    }
    private fun initRecyclerViewTask(view: View){
        val recylcerView = view.findViewById<RecyclerView>(R.id.rvListTask)
        recylcerView.layoutManager= LinearLayoutManager(activity)
        recAdapterIsiTask= AdapterIsiTask(listTask,null,true)
        recylcerView.adapter=recAdapterIsiTask
    }

    private fun showDate(){
        val datePickerFragment = datePickerFragment(this)
        fragmentManager?.let { datePickerFragment.show(it,"datefragment") }
    }

     class datePickerFragment(val dateSelected: DateSelected):DialogFragment(),DatePickerDialog.OnDateSetListener{
        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
            return DatePickerDialog(requireContext(),this,year,month,dayOfMonth)
        }
        override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {

            dateSelected.receiveDate(p1,p2+1,p3)
        }

    }
    private fun TambahTaskFS(){
        if(etDate.text.toString()!=""){
            if(listTask.size > 0 ){
                val items =HashMap<String,Any>()
                items.put("tanggal",etDate.text.toString())
                items.put("listTask",listTask)
                Date?.let { items.put("dateTime", it) }
                uid?.let { items.put("uid", it) }
                val collection=db.collection("task").document()
                collection.set(items).addOnSuccessListener {
                    Toast.makeText(this.context,"Berhasil Menambah Task",Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }.addOnFailureListener {
                    Toast.makeText(this.context,it.toString(),Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this.context,"Tidak Menemukan Task",Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this.context,"Tolong isi Tanggal",Toast.LENGTH_SHORT).show()
        }
    }

    override fun receiveDate(year: Int, month: Int, dayOfMonth: Int) {
        etDate.setText("$dayOfMonth/$month/$year")
        Date= Date(year,month-1,dayOfMonth)
        Log.d("date",Date.toString())
    }

    private fun addInfo(){
        val inflater = LayoutInflater.from(this.requireContext())
        val v = inflater.inflate(R.layout.dialog_add_task,null)

        val task = v.findViewById<EditText>(R.id.etNamaTask)
        val addDialog = AlertDialog.Builder(this.requireContext())

        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            if (task.text.toString()!=""){
                val taskname =task.text.toString()
                var items=IsiTask()
                items.isi=taskname
                items.checked=false
                listTask.add(items)
                recAdapterIsiTask.notifyDataSetChanged()
                Toast.makeText(this.context,"Add task Success", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }else{
                Toast.makeText(this.context,"Tolong isi data dengan lengkap", Toast.LENGTH_SHORT).show()
            }

        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
            Toast.makeText(this.context,"Canceling Add Item", Toast.LENGTH_SHORT).show()
        }
        addDialog.create()
        addDialog.show()
    }
}

interface DateSelected{
    fun receiveDate(year:Int,month:Int,dayOfMonth:Int)
}