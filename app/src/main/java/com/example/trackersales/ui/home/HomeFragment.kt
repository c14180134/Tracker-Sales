package com.example.trackersales.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.trackersales.MainActivityBottomNav
import com.example.trackersales.R
import com.example.trackersales.databinding.FragmentHomeBinding
import com.example.trackersales.dataclass.Orders
import com.example.trackersales.room.Location
import com.example.trackersales.room.LocationDB
import com.example.trackersales.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!


    var historyOrderArrayList : ArrayList<Orders> = ArrayList()
    private lateinit var db: FirebaseFirestore
    var uid = ""
    var totalOrder = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val simpleDate = SimpleDateFormat("dd/M/yyyy")
        val currentDate = simpleDate.format(Date())

        val user = FirebaseAuth.getInstance().currentUser
        var namaUser =""

        user?.let {
           namaUser = user.email.toString()
            uid=user.uid
        }

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        historyOrderArrayList.clear()
        fetchDataUserOrder()
        binding.tvTotalOrder.visibility=View.GONE
        Handler().postDelayed({
            binding.tvTotalOrder.text = historyOrderArrayList.size.toString()
            binding.progressBar2.visibility=View.GONE
            binding.tvTotalOrder.visibility=View.VISIBLE
            binding.cardCheckin.setOnClickListener{
                findNavController().navigate(R.id.action_navigation_home_to_check_in_Fragment)
            }
            binding.cardHistoryOrder.setOnClickListener {
                findNavController().navigate(R.id.action_navigation_home_to_historyOrder)
            }

            binding.cardorder.setOnClickListener{
                findNavController().navigate(R.id.action_navigation_home_to_createOrderFragment)
            }
                              }, 700)



        if(loadSharedPre()){
            binding.btnMasukKerja.isEnabled=false
        }else{
            binding.btnSelesaiKerja.isEnabled=false
        }


        navBar.visibility = View.VISIBLE
        val btnCreateOrder: CardView = binding.cardorder
        if(isAdmin()){
            binding.cardLogOutSales.visibility=View.GONE
            binding.cardCheckin.visibility=View.GONE
        }else{
            binding.createNotificationBtn.visibility=View.GONE
            binding.cardLogOutAdmin.visibility=View.GONE
        }


        binding.tvSalesName.text=namaUser
        binding.tvTanggalHariIni.text=currentDate.toString()

        binding.cardLogOutSales.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this.context,LoginActivity::class.java)
            startActivity(intent);
            this.activity?.finish()
        }
        binding.cardLogOutAdmin.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this.context,LoginActivity::class.java)
            startActivity(intent);
            this.activity?.finish()
        }

        binding.tvLihatDetail.setOnClickListener {
            findNavController().navigate(R.id.detail_User)
        }

        binding.createNotificationBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_pushNotification)
        }

        binding.btnMasukKerja.setOnClickListener {
            (activity as MainActivityBottomNav).startLocationService()

            binding.btnSelesaiKerja.isEnabled=true
            binding.btnMasukKerja.isEnabled=false
            val sharedPref = activity?.getSharedPreferences("LocActive", Context.MODE_PRIVATE)
            val editors=sharedPref?.edit()
            editors?.putBoolean("IS_LOC_SERVICE_ON",true)
            editors?.commit()

        }
        binding.btnSelesaiKerja.setOnClickListener {
            (activity as MainActivityBottomNav).stopLocationService()
            binding.btnSelesaiKerja.isEnabled=false
            binding.btnMasukKerja.isEnabled=true
            val sharedPref = activity?.getSharedPreferences("LocActive", Context.MODE_PRIVATE)
            val editors=sharedPref?.edit()
            editors?.putBoolean("IS_LOC_SERVICE_ON",false)
            editors?.commit()
        }



        return root
    }

    override fun onStart() {
        super.onStart()
        startLoading()


    }



    fun loadSharedPre(): Boolean {
        val sharedPref = activity?.getSharedPreferences("LocActive", Context.MODE_PRIVATE)
        val islocon=sharedPref?.getBoolean("IS_LOC_SERVICE_ON",false)
        if(islocon!=true){
            return false
        }
        return true
    }
    fun isAdmin(): Boolean {
        val sharedPref = activity?.getSharedPreferences("LocActive", Context.MODE_PRIVATE)
        var IS_ADMIN= sharedPref?.getBoolean("IS_ADMIN",false)!!
        if(IS_ADMIN!=true){
            return false
        }
        return true
    }

    fun fetchDataUserOrder(){

        db = FirebaseFirestore.getInstance()
        db.collection("orders").
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
                        val x =dc.document.toObject(Orders::class.java)
                        if(x.sales_id.toString()== uid){
                            totalOrder++
                            historyOrderArrayList.add(dc.document.toObject(Orders::class.java))
                        }
                    }
                }

            }

        })

    }

    fun startLoading(){
        var loadingDialog: AlertDialog
        val dialogLoading=layoutInflater.inflate(R.layout.custom_loading_dialog,null)
        val builder = AlertDialog.Builder(this.context)
        builder.setView(dialogLoading)
        builder.setCancelable(false)
        loadingDialog=builder.create()
        loadingDialog.show()
        loadingDialog.window?.setBackgroundDrawableResource(R.drawable.camera_checkin_pic)
        loadingDialog.window?.setLayout(600,500)
        Handler().postDelayed({
            loadingDialog.dismiss()
        }, 500)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}