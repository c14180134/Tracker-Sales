package com.example.trackersales.ui.customers

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.dataclass.UserCustomer
import com.example.trackersales.adapter.RecAdapterCustomer
import com.example.trackersales.databinding.FragmentCustomerBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.text.SimpleDateFormat
import java.util.*


class CustomersFragment : Fragment() {
    private var _binding: FragmentCustomerBinding? = null

    private lateinit var recAdapterCustomer: RecAdapterCustomer
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var db: FirebaseFirestore
    var customerArrayList :ArrayList<UserCustomer> = ArrayList()
    private var longitude = 0.0
    private  var latitude = 0.0

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())



        _binding = FragmentCustomerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.VISIBLE



        _binding!!.btnTambahCustomer.setOnClickListener {
            fetchLocation()
        }


        eventChangeListenerCustomer()
        initRecyclerViewCustomer(root)
        return root
    }

    private fun fetchLocation() {
        val task = fusedLocationClient.lastLocation
        checkLocPermission()
        task.addOnSuccessListener {

            if(it!=null){
                longitude=it.longitude
                latitude=it.latitude
                Handler().postDelayed({
                    dialogCustomer()
                }, 100)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun checkLocPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(this.requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(this.requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED
        ){
            return true
        }
        return false
    }

    private fun dialogCustomer() {
        val simpleDate = SimpleDateFormat("dd/M/yyyy")
        val currentDate = simpleDate.format(Date())


        db = FirebaseFirestore.getInstance()

        val inflater = LayoutInflater.from(this.requireContext())
        val v = inflater.inflate(R.layout.dialog_add_customer, null)
        val namaCustomer = v.findViewById<EditText>(R.id.etNamaTambahCustomer)
        val alamatCustomer = v.findViewById<EditText>(R.id.etAlamatTambahCustomer)
        val noTeleponCustomer= v.findViewById<EditText>(R.id.etNoTeleponTambahCustomer)
        val addDialog = AlertDialog.Builder(this.requireContext())
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            if (namaCustomer.text.toString()!=""&&alamatCustomer.text.toString()!=""&&noTeleponCustomer.text.toString()!=""){
                val items =HashMap<String,Any>()
                items.put("name",namaCustomer.text.toString())
                items.put("tanggal",currentDate)
                items.put("alamat",alamatCustomer.text.toString())
                items.put("notelepon",noTeleponCustomer.text.toString().toLong())
                val document =db.collection("customer").document()
                items.put("alamatLong",longitude)
                items.put("alamatLat",latitude)
                items.put("customerid",document.id)
                val set = document.set(items)
                set.addOnSuccessListener {
                    Toast.makeText(this.context,"success",Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                set.addOnFailureListener {
                    Toast.makeText(this.context, it.toString(), Toast.LENGTH_SHORT).show()
                }

            }else{

                Toast.makeText(this.context,"Tolong isi data dengan lengkap",Toast.LENGTH_SHORT).show()
            }

        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->

            dialog.dismiss()
        }
        addDialog.setView(v)
        addDialog.create()
        addDialog.show()

    }


//    private fun getCurrentLocation() {
//        // checking location permission
//        if (ActivityCompat.checkSelfPermission(requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // request permission
//            ActivityCompat.requestPermissions(requireActivity(),
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQ_CODE);
//            return
//        }
//        fusedLocationClient.lastLocation
//            .addOnSuccessListener { location ->
//                // getting the last known or current location
//                latitude = location.latitude
//                longitude = location.longitude
//                dialogCustomer()
//            }
//            .addOnFailureListener {
//                Toast.makeText(this.context,"Please enable your location permission First",Toast.LENGTH_SHORT).show()
//            }
//    }

    private fun initRecyclerViewCustomer(view: View){
        val recylcerView = view.findViewById<RecyclerView>(R.id.recSalesView)
        recylcerView.layoutManager=LinearLayoutManager(activity)
        recAdapterCustomer=RecAdapterCustomer(customerArrayList)
        recylcerView.adapter=recAdapterCustomer

    }

    private fun eventChangeListenerCustomer(){
        db = FirebaseFirestore.getInstance()
        db.collection("customer").
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

                        customerArrayList.add(dc.document.toObject(UserCustomer::class.java))
                    }
                }
                recAdapterCustomer.notifyDataSetChanged()
            }
        })
    }


}