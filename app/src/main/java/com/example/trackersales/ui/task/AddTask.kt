package com.example.trackersales.ui.task

import android.app.DatePickerDialog
import android.app.Dialog
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.adapter.AdapterIsiTask
import com.example.trackersales.databinding.FragmentAddTaskBinding



class AddTask : Fragment(),DateSelected {
    val listTask = ArrayList<String>()
    private var uid: String? = null

    private var _binding: FragmentAddTaskBinding? = null

    private val binding get() = _binding!!
    private lateinit var recAdapterIsiTask: AdapterIsiTask
    lateinit var etDate :EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString("uid")
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAddTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root
        etDate=binding.etDate
        binding.btnDate.setOnClickListener {
            showDate()
        }

        return root
    }
    private fun initRecyclerViewTask(view: View){
        val recylcerView = view.findViewById<RecyclerView>(R.id.rvListTask)
        recylcerView.layoutManager= LinearLayoutManager(activity)
        recAdapterIsiTask= AdapterIsiTask(listTask,true)
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
            dateSelected.receiveDate(p1,p2,p3)
        }

    }

    override fun receiveDate(year: Int, month: Int, dayOfMonth: Int) {
        etDate.setText("$dayOfMonth/$month/$year")
    }

}

interface DateSelected{
    fun receiveDate(year:Int,month:Int,dayOfMonth:Int)
}