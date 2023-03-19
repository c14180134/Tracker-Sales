package com.example.trackersales.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.adapter.RecAdapterSales
import com.example.trackersales.dataclass.UserSales
import com.example.trackersales.databinding.FragmentDashboardBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class DashboardFragment : Fragment() {
    var salesArrayList : ArrayList<UserSales> = ArrayList()
    private lateinit var db: FirebaseFirestore
    private lateinit var dashboardViewModel: DashboardViewModel
    private var _binding: FragmentDashboardBinding? = null

    private lateinit var recAdapterSales: RecAdapterSales

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.VISIBLE

        salesArrayList.clear()

        eventChangeListener()
        initRecyclerViewSales(root)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun initRecyclerViewSales(view: View){
        val recylcerView = view.findViewById<RecyclerView>(R.id.recSalesView)
        recylcerView.layoutManager= LinearLayoutManager(activity)
        recAdapterSales= RecAdapterSales(salesArrayList)
        recylcerView.adapter=recAdapterSales

    }

    private fun eventChangeListener(){
        val user = FirebaseAuth.getInstance().currentUser
        var uid =""

        user?.let {
            uid=user.uid
        }
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
                        val pengecualian = dc.document.toObject(UserSales::class.java)
                        if(pengecualian.UID.toString()!=uid){
                            if(pengecualian.UID!=null){
                                salesArrayList.add(dc.document.toObject(UserSales::class.java))
                            }

                        }

                    }
                }
                recAdapterSales.notifyDataSetChanged()
            }
        })
    }
}