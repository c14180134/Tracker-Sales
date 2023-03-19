package com.example.trackersales.ui.task

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.adapter.AdapterMainTask
import com.example.trackersales.databinding.FragmentTaskBinding
import com.example.trackersales.dataclass.TanggalTask
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.*

class Task : Fragment() {
    private var _binding: FragmentTaskBinding? = null

    private val binding get() = _binding!!
    private lateinit var recAdapterMainTask: AdapterMainTask
    private var uid: String? = null
    private var email: String? = null
    private var listTask:ArrayList<TanggalTask> = ArrayList()

    private lateinit var db : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            uid = it.getString("UID")
            email=it.getString("email")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.GONE
        binding.tvNamaSales.text=email
        db = FirebaseFirestore.getInstance()
        FetchTask()
        val bundle = Bundle()
        bundle.putString("UID",uid)
        initRecyclerViewTask(root)

        binding.btnBack.setOnClickListener {
            it.findNavController().navigateUp()
        }
        if(isAdmin()!=true){
            binding.fabTambahTask.visibility=View.GONE
        }

        binding.fabTambahTask.setOnClickListener{
            it.findNavController().navigate(R.id.addTask,bundle)
        }

        return root
    }

    private fun initRecyclerViewTask(view: View){
        val recylcerView = view.findViewById<RecyclerView>(R.id.rvTaskTanggalSales)
        recylcerView.layoutManager= LinearLayoutManager(activity)
        recAdapterMainTask= AdapterMainTask(requireContext(),listTask)
        recylcerView.adapter=recAdapterMainTask

    }
    fun isAdmin(): Boolean {
        val sharedPref = activity?.getSharedPreferences("LocActive", Context.MODE_PRIVATE)
        var IS_ADMIN= sharedPref?.getBoolean("IS_ADMIN",false)!!
        if(IS_ADMIN!=true){
            return false
        }
        return true
    }

    fun FetchTask(){
        listTask.clear()
        val query= db.collection("task").whereEqualTo("uid",uid).orderBy("dateTime")
        query.addSnapshotListener(object : EventListener<QuerySnapshot> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onEvent(
                value: QuerySnapshot?,
                error: FirebaseFirestoreException?
            ) {
                if(error !=null){
                    Log.e("Firestore Error",error.message.toString())
                    return
                }
                for(dc : DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        Log.d("asw",dc.document.toString())
                       listTask.add(dc.document.toObject(TanggalTask::class.java))
                    }
                }
                recAdapterMainTask.notifyDataSetChanged()
            }
        })
    }

}