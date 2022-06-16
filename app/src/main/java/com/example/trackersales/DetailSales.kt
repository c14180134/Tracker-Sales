package com.example.trackersales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.trackersales.databinding.FragmentDetailSalesBinding
import com.example.trackersales.databinding.FragmentDetailUserBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailSales : Fragment() {
    private var _binding: FragmentDetailSalesBinding? = null

    private val binding get() = _binding!!
    private var uidRecylcer: String? = null
    private var email: String? = null
    private var tanggalProgress: String? = null
    private var targetRecycler: Long? = null
    private var currentProgress: Long? = null
    private lateinit var db : FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            email=it.getString("email")
            uidRecylcer = it.getString("UID")
            targetRecycler = it.getLong("target")
            currentProgress= it.getLong("currentprogress")
            tanggalProgress=it.getString("tanggalprogress")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailSalesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.GONE
        db = FirebaseFirestore.getInstance()
        binding.tvDetailNama.text=email
        binding.tvTargetDetail.text="Rp."+targetRecycler.toString()
        binding.tvCurrentProgress.text="Rp."+currentProgress.toString()

        val bundle = Bundle()
        bundle.putString("UID",uidRecylcer)
        bundle.putString("email",email)

        binding.btnBack.setOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.buttonListHistoriPerjalanan.setOnClickListener {
            it.findNavController().navigate(R.id.listHistoryPerjalanan,bundle)
        }

        binding.btnTask.setOnClickListener {
            it.findNavController().navigate(R.id.task,bundle)
        }

        return root
    }
}