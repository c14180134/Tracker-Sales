package com.example.trackersales.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.RecAdapterSales
import com.example.trackersales.UserSales
import com.example.trackersales.databinding.FragmentCustomerBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.*

class NotificationsFragment : Fragment() {

    private lateinit var notificationsViewModel: NotificationsViewModel
    private var _binding: FragmentCustomerBinding? = null
    private lateinit var recyclerView: RecyclerView

    private lateinit var recAdapterSales: RecAdapterSales
    private lateinit var db: FirebaseFirestore
    var salesArrayList :ArrayList<UserSales> = ArrayList()
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)
        _binding = FragmentCustomerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.VISIBLE


        notificationsViewModel.text.observe(viewLifecycleOwner, Observer {

        })

        eventChangeListener()
        initRecyclerView(root)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initRecyclerView(view: View){
        val recylcerView = view.findViewById<RecyclerView>(R.id.recSalesView)
        recylcerView.layoutManager=LinearLayoutManager(activity)
        recAdapterSales=RecAdapterSales(salesArrayList)
        recylcerView.adapter=recAdapterSales

    }

    private fun eventChangeListener(){
        db = FirebaseFirestore.getInstance()
        db.collection("users").
        addSnapshotListener(object : EventListener<QuerySnapshot> {
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

                        salesArrayList.add(dc.document.toObject(UserSales::class.java))
                    }
                }
                recAdapterSales.notifyDataSetChanged()
            }
        })
    }
}