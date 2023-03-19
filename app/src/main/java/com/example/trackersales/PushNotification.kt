package com.example.trackersales

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import com.example.trackersales.adapter.AdapterListCustomerDialog
import com.example.trackersales.adapter.AdapterListSalesDialog
import com.example.trackersales.dataclass.Notifikasi
import com.example.trackersales.dataclass.PushNotifikasi
import com.example.trackersales.dataclass.UserCustomer
import com.example.trackersales.dataclass.UserSales
import com.example.trackersales.forfirebasecloudmessaging.RetrofitInstance
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

const val TOPIC = "/topics/myTopic2"

class PushNotification : Fragment() {

    val TAG="Halosd"
    private lateinit var btnSend:Button
    private lateinit var btnPilihSales:Button
    private lateinit var etTitle:EditText
    private lateinit var etMessage:EditText
    private lateinit var tvSalesName:TextView
    private lateinit var ListViewSales :ListView
    private lateinit var SalesAdapter: AdapterListSalesDialog
    var tempArrayList= ArrayList<UserSales>()
    var listSalesDialog= ArrayList<UserSales>()
    private lateinit var db : FirebaseFirestore
    var FCMToken = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =inflater.inflate(R.layout.fragment_push_notification, container, false)
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
        btnSend = view.findViewById(R.id.btnPushNotif)
        etTitle=view.findViewById(R.id.judulEt)
        etMessage=view.findViewById(R.id.etIsiNotifikasi)
        btnPilihSales = view.findViewById(R.id.btnPilihSales)
        tvSalesName=view.findViewById(R.id.tvSalesName)
        fetchDataSalesDialog()
        btnPilihSales.setOnClickListener{

            dialogSalesPop()
        }

        btnSend.setOnClickListener {
            val title = etTitle.text.toString()
            val message = etMessage.text.toString()
            if(title.isNotEmpty() && message.isNotEmpty()) {
                if(FCMToken==""){
                    Toast.makeText(this.context,"Tolong pilih User untuk dinotifikasi", Toast.LENGTH_SHORT).show()
                }
                else{
                    PushNotifikasi(
                        Notifikasi(title,message), FCMToken
                    ).also {
                        sendNotification(it)
                    }
                    it.findNavController().navigateUp()
                }

            }else{
                Toast.makeText(this.context,"Tolong isi data dengan lengkap", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            it.findNavController().navigateUp()
        }

        return view
    }

    private fun fetchDataSalesDialog(){
        db = FirebaseFirestore.getInstance()
        db.collection("users").
        addSnapshotListener(object : EventListener<QuerySnapshot> {
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error !=null){
                    Log.e("Error",error.message.toString())
                    return
                }

                for(dc: DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){

                        if(dc.document["email"]!=null&&dc.document["tokenFCM"]!=null){
                            listSalesDialog.add(dc.document.toObject(UserSales::class.java))
                            tempArrayList.add(dc.document.toObject(UserSales::class.java))
                        }


                    }
                }

            }
        })
    }

    private fun dialogSalesPop(){
        val inflater = LayoutInflater.from(this.requireContext())
        val v = inflater.inflate(R.layout.custom_dialog_listviewsales_notification,null)
        ListViewSales = v.findViewById(R.id.ListSalesName)
        val searchViewCustomer = v.findViewById<SearchView>(R.id.searchbar_sales_name)
        SalesAdapter = AdapterListSalesDialog(this.requireContext(),tempArrayList)
        ListViewSales.adapter=SalesAdapter
        var addDialog = AlertDialog.Builder(this.context)



        searchViewCustomer.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newTexts: String?): Boolean {
                tempArrayList.clear()
                val searchText = newTexts!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    listSalesDialog.forEach {
                        if(it.email!!.toLowerCase(Locale.getDefault()).contains(searchText)){
                            tempArrayList.add(it)
                        }
                    }
                    SalesAdapter.notifyDataSetChanged()
                }else{
                    tempArrayList.addAll(listSalesDialog)
                    SalesAdapter.notifyDataSetChanged()
                }
                return false
            }

        })
        var dialogs = addDialog.show()
        addDialog.setView(v)
        addDialog.create()
        addDialog.show()
        ListViewSales.setOnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(this.context,tempArrayList[i].email, Toast.LENGTH_SHORT).show()
            tvSalesName.visibility=View.VISIBLE
            tvSalesName.setText(tempArrayList[i].email)
            FCMToken= tempArrayList[i].tokenFCM!!
            btnPilihSales.setText("Ganti Sales")
            dialogs.dismiss()
        }
    }

    private fun sendNotification(notification: PushNotifikasi) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful) {
                Log.d(TAG, "Response: ${Gson().toJson(response)}")
            } else {
                Log.e(TAG, response.errorBody().toString())
            }
        } catch(e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

}