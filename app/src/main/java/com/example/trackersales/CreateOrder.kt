package com.example.trackersales

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.adapter.AdapterItem
import com.example.trackersales.adapter.AdapterListCustomerDialog
import com.example.trackersales.dataclass.Item
import com.example.trackersales.dataclass.UserCustomer
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class CreateOrder : Fragment() {
    private lateinit var viewOfLayout: View
    private lateinit var listView :RecyclerView
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
    var datetime:Date?=null
    var totalHargaorderlist=0
    private lateinit var addDialog :AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FetchItem()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val testDate = "29-Apr-2010,13:00:14 PM"
        val formatter: DateFormat = SimpleDateFormat("d-MMM-yyyy,HH:mm:ss aaa")
        val date: Date = formatter.parse(testDate)
        addDialog = AlertDialog.Builder(this.context)
        datetime=date

        val navBar: BottomNavigationView = requireActivity().findViewById(R.id.nav_view)
        navBar.visibility = View.GONE
        // Inflate the layout for this fragment
        viewOfLayout = inflater!!.inflate(R.layout.fragment_create_order, container, false)

        initRecyclerViewTask(viewOfLayout)

        addItemButton =viewOfLayout.findViewById(R.id.itemAddBtnOrder)
        tvNamaCustomer = viewOfLayout.findViewById(R.id.tvCustomerName)
        pilihCustomerButton =viewOfLayout.findViewById(R.id.btnPilihCustomer)
        createOrderBtn = viewOfLayout.findViewById(R.id.createOrderBtn)
        tvTotalHargaOrder=viewOfLayout.findViewById(R.id.tvTotalHarga)
        val recylcerView = viewOfLayout.findViewById<RecyclerView>(R.id.itemListView)

        recylcerView.setOnClickListener {
            for(i in listItemOrder){
                totalHargaorderlist=totalHargaorderlist + i.harga!!.toInt()
            }
//                totalHargaorderlist= (totalHargaorderlist+x).toInt()
            tvTotalHargaOrder.text=totalHargaorderlist.toString()
        }

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
    private fun initRecyclerViewTask(view: View){
        val recylcerView = view.findViewById<RecyclerView>(R.id.itemListView)
        recylcerView.layoutManager= LinearLayoutManager(activity)
        itemAdapter= AdapterItem(view,listItemOrder!!,false)
        recylcerView.adapter=itemAdapter

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
            if(listItemOrder.size > 0 ){
                val sharedPref = activity?.getSharedPreferences("LocActive", Context.MODE_PRIVATE)
                var uidsp= sharedPref?.getString("UID","")
                val items =HashMap<String,Any>()
                items.put("dateTime", LocalDateTime.now())
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
                var queryCustomer= db.collection("customer").whereEqualTo("customerid",cid).get()
                queryCustomer.addOnSuccessListener {
                    val items = java.util.HashMap<String, Any>()
                    val jumlah = it.documents[0].get("seluruhpengeluaran").toString().toDouble()+totalHarga
                    val totalPembelian = it.documents[0].get("totalbeli").toString().toInt()+1
                    items.put("seluruhpengeluaran",jumlah)
                    items.put("totalbeli",totalPembelian)

                    for(document in it){
                        db.collection("customer").document(document.id).set(items, SetOptions.merge())
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
                        if(dc.document["name"]!=null){
                            listCustomerDialog.add(dc.document.toObject(UserCustomer::class.java))
                            tempArrayList.add(dc.document.toObject(UserCustomer::class.java))
                        }


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
                    tempArrayList.addAll(listCustomerDialog)
                    CustomerAdapter.notifyDataSetChanged()
                }
                return false
            }

        })
        var dialogs = addDialog.show()
        addDialog.setView(v)
        addDialog.create()
        addDialog.show()
        listViewCustomer.setOnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(this.context,tempArrayList[i].name,Toast.LENGTH_SHORT).show()
            tvNamaCustomer.visibility=View.VISIBLE
            tvNamaCustomer.setText(tempArrayList[i].name)
            pilihCustomerButton.setText("Ganti Customer")
            dialogs.dismiss()
        }
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
//                for(i in listItemOrder){
//                    totalHargaorderlist=totalHargaorderlist + i.harga!!.toInt()
//                }
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