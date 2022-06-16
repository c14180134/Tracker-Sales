package com.example.trackersales

import android.app.AlertDialog
import android.content.Context
import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController

import com.example.trackersales.adapter.AdapterItem
import com.example.trackersales.adapter.AdapterListCustomerDialog
import com.example.trackersales.dataclass.Item
import com.example.trackersales.dataclass.UserCustomer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CreateOrder : Fragment() {
    private lateinit var viewOfLayout: View
    private lateinit var listView :ListView
    private lateinit var listViewCustomer :ListView
    private lateinit var tvNamaCustomer:TextView
    private lateinit var tvTotalHargaOrder:TextView
    private lateinit var pilihCustomerButton:Button
    private lateinit var addItemButton:Button
    private lateinit var createOrderBtn:Button

    var tempArrayList= ArrayList<UserCustomer>()
    val listItemOrder = mutableListOf<Item>()
    val listItemDialog = ArrayList<Item>()
    val listCustomerDialog= ArrayList<UserCustomer>()
    private lateinit var itemAdapter: AdapterItem
    private lateinit var CustomerAdapter: AdapterListCustomerDialog
    private lateinit var db : FirebaseFirestore
    var listItemName = ArrayList<String>()
    var listCustomerName = ArrayList<String>()

    var totalHargaorderlist=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FetchItem()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.GONE
        // Inflate the layout for this fragment
        viewOfLayout = inflater!!.inflate(R.layout.fragment_create_order, container, false)

        itemAdapter = AdapterItem(this.requireContext(),listItemOrder)
        listView = viewOfLayout.findViewById(R.id.itemListView)

        addItemButton =viewOfLayout.findViewById(R.id.itemAddBtnOrder)
        tvNamaCustomer = viewOfLayout.findViewById(R.id.tvCustomerName)
        pilihCustomerButton =viewOfLayout.findViewById(R.id.btnPilihCustomer)
        createOrderBtn = viewOfLayout.findViewById(R.id.createOrderBtn)
        tvTotalHargaOrder=viewOfLayout.findViewById(R.id.tvTotalHarga)
        listView.adapter = itemAdapter

        viewOfLayout.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            it.findNavController().navigateUp()
        }

        pilihCustomerButton.setOnClickListener{

            dialogCustomer()
        }


        addItemButton.setOnClickListener{

            addInfo()
        }

        createOrderBtn.setOnClickListener {
            storeOrder()

        }



        return viewOfLayout

    }

    private fun storeOrder(){
        val simpleDate = SimpleDateFormat("dd/M/yyyy")
        val currentDate = simpleDate.format(Date())

        db = FirebaseFirestore.getInstance()
        var totalHarga = 0.0

        var cid =""
        for(i in listItemOrder){
            totalHarga+= i.harga!!
        }
//        val user = FirebaseAuth.getInstance().currentUser
//        var uid =""
//        user?.let {
//            uid = user.uid
//        }

        for(k in listCustomerDialog){
            if(tvNamaCustomer.text.toString()==k.name){
                cid=k.customerid.toString()
            }
        }
        if(tvNamaCustomer.text !="Nama"){
            Log.d("halo","masuk1")
            if(listItemOrder.size > 0 ){
                val sharedPref = activity?.getSharedPreferences("LocActive", Context.MODE_PRIVATE)
                var uidsp= sharedPref?.getString("UID","")
                val items =HashMap<String,Any>()
                items.put("nama_customer",tvNamaCustomer.text.toString())
                items.put("tanggal",currentDate)
                items.put("total_harga",totalHarga)
                items.put("customer_id",cid)
                items.put("item",listItemOrder)
                items.put("sales_id",uidsp?:"")
                val collection=db.collection("orders").document()
                items.put("uniqueid",collection.id)
                collection.set(items).addOnSuccessListener {
                    Toast.makeText(this.context,"success Create Order",Toast.LENGTH_SHORT).show()
                    findNavController().navigateUp()
                }.addOnFailureListener {
                    Toast.makeText(this.context,"it",Toast.LENGTH_SHORT).show()
                }
                var query= db.collection("users").whereEqualTo("uid",uidsp).get()
                query.addOnSuccessListener {
                    val items = java.util.HashMap<String, Any>()
                    val jumlah = it.documents[0].get("currentprogress").toString().toDouble()+totalHarga
                    val todaySold = it.documents[0].get("todaysold").toString().toInt()+1
                    items.put("currentprogress",jumlah)
                    items.put("todaysold",todaySold)
                    for(document in it){
                        db.collection("users").document(document.id).set(items, SetOptions.merge())
                    }
                }

            }
        }else if(tvNamaCustomer.text=="Nama"){
            Toast.makeText(this.context,"tolong pilih nama customer",Toast.LENGTH_SHORT).show()
        }

    }

    private fun FetchItem(){
        db = FirebaseFirestore.getInstance()
        db.collection("item").
            addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error !=null){
                        Log.e("Error",error.message.toString())
                        return
                    }

                    for(dc:DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED){
//                            Log.d("isi document",dc.document.toObject(Item::class.java).toString())
                            listItemName.add(dc.document["nama"].toString())
                            listItemDialog.add(dc.document.toObject(Item::class.java))

                        }
                    }

                }
            })
        db.collection("customer").
        addSnapshotListener(object : EventListener<QuerySnapshot>{
            override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                if(error !=null){
                    Log.e("Error",error.message.toString())
                    return
                }

                for(dc:DocumentChange in value?.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        listCustomerName.add(dc.document["nama"].toString())
                        listCustomerDialog.add(dc.document.toObject(UserCustomer::class.java))

                    }
                }

            }
        })
    }

    private fun dialogCustomer(){
        val inflater = LayoutInflater.from(this.requireContext())
        val v = inflater.inflate(R.layout.dialog_listview_customer,null)
        listViewCustomer = v.findViewById(R.id.list_view_customer_dialog)
        val searchViewCustomer = v.findViewById<SearchView>(R.id.search_bar_customer_name)
        CustomerAdapter = AdapterListCustomerDialog(this.requireContext(),tempArrayList)
        listViewCustomer.adapter=CustomerAdapter

        listViewCustomer.setOnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(this.context,tempArrayList[i].name,Toast.LENGTH_SHORT).show()
            tvNamaCustomer.visibility=View.VISIBLE
            tvNamaCustomer.setText(tempArrayList[i].name)
            pilihCustomerButton.setText("Ganti Customer")
        }


        searchViewCustomer.setOnQueryTextListener(object :SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newTexts: String?): Boolean {
                tempArrayList.clear()
                val searchText = newTexts!!.toLowerCase(Locale.getDefault())
                if (searchText.isNotEmpty()){
                    listCustomerDialog.forEach {
                        if(it.name!!.toLowerCase(Locale.getDefault()).contains(searchText)){
                            tempArrayList.add(it)
                        }
                    }
                    CustomerAdapter.notifyDataSetChanged()
                }else{
                    tempArrayList.clear()
                    tempArrayList.addAll(listCustomerDialog)
                    CustomerAdapter.notifyDataSetChanged()
                }
                return false
            }

        })
        val addDialog = AlertDialog.Builder(this.requireContext())
        addDialog.setView(v)
        addDialog.create()
        addDialog.show()
    }

    private fun addInfo(){
        val inflater = LayoutInflater.from(this.requireContext())
        val v = inflater.inflate(R.layout.add_dialog_item_box,null)

        val jumlahItem = v.findViewById<EditText>(R.id.etJumlahItem)
        val addDialog = AlertDialog.Builder(this.requireContext())
        val arrayAdapter= ArrayAdapter(requireContext(),R.layout.dropdown_item_dialog_order,listItemName)

        val autoCtTv= v.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        autoCtTv.setAdapter(arrayAdapter)
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
            dialog,_->
            if (jumlahItem.text.toString()!=""){
                val name=autoCtTv.text.toString()
                val jumlah =jumlahItem.text.toString()
                var harga=0

                for(x in listItemDialog){
                    if(x.nama==name){
                        harga= x.harga!!.toInt()
                        break
                    }

                }
                val x = jumlah.toLong()*harga
                totalHargaorderlist= (totalHargaorderlist+x).toInt()
                tvTotalHargaOrder.text=totalHargaorderlist.toString()
                listItemOrder.add(Item((harga*jumlah.toLong()),name,jumlah.toLong()))
                itemAdapter.notifyDataSetChanged()
                Toast.makeText(this.context,"Add Item Success",Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }else{

                Toast.makeText(this.context,"Tolong isi data dengan lengkap",Toast.LENGTH_SHORT).show()
            }

        }
        addDialog.setNegativeButton("Cancel"){
            dialog,_->
            dialog.dismiss()
            Toast.makeText(this.context,"Canceling Add Item",Toast.LENGTH_SHORT).show()
        }
        addDialog.create()
        addDialog.show()
    }


}