package com.example.trackersales

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.findNavController
import com.example.trackersales.databinding.FragmentDetailSalesBinding
import com.example.trackersales.databinding.FragmentDetailUserBinding
import com.example.trackersales.dataclass.IsiTask
import com.example.trackersales.dataclass.Orders
import com.example.trackersales.dataclass.UserSales
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class DetailSales : Fragment() {
    private var _binding: FragmentDetailSalesBinding? = null
    var historyOrderArrayList : ArrayList<Orders> = ArrayList()
    private val binding get() = _binding!!
    private var uidRecylcer: String? = null
    private var email: String? = null
    private var tanggalProgress: String? = null
    private var targetRecycler: Long? = null
    private var currentProgress: Long? = null
    private lateinit var db : FirebaseFirestore

    private var dataTotalOrderBulanan = ArrayList<Int>()
    private var dataNWOrderBulanan = ArrayList<Float>()

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
        dataTotalOrderBulanan= arrayListOf(0,0,0,0,0,0,0,0,0,0,0,0)
        dataNWOrderBulanan= arrayListOf(0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f,0f)
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
        getOrderData()
        binding.btnBack.setOnClickListener {
            it.findNavController().navigateUp()
        }

        binding.btnTargetLokasi.setOnClickListener {
            it.findNavController().navigate(R.id.targetLocation,bundle)
        }

        binding.buttonListHistoriPerjalanan.setOnClickListener {
            it.findNavController().navigate(R.id.listHistoryPerjalanan,bundle)
        }

        binding.btnLihatOrderSales.setOnClickListener {
            it.findNavController().navigate(R.id.historyOrder,bundle)
        }

        binding.btnListLaporan.setOnClickListener {
            it.findNavController().navigate(R.id.list_Laporan,bundle)
        }

        binding.btnTask.setOnClickListener {
            it.findNavController().navigate(R.id.task,bundle)
        }

        binding.btnEditTarget.setOnClickListener {
            addInfo()
        }
        return root
    }

    private fun addInfo(){
        val inflater = LayoutInflater.from(this.requireContext())
        val v = inflater.inflate(R.layout.dialog_edit_target,null)

        val target = v.findViewById<EditText>(R.id.etTarget)
        val addDialog = AlertDialog.Builder(this.requireContext())
        target.setText(targetRecycler.toString())
        addDialog.setView(v)
        addDialog.setPositiveButton("Ok"){
                dialog,_->
            if (target.text.toString()!=""){
                val target =target.text.toString()
                var sales= db.collection("users")
                var query= sales.whereEqualTo("uid",uidRecylcer).get()
                query.addOnSuccessListener {
                    var items=HashMap<String,Any>()
                    items.put("target",target.toLong())
                    items.put("currentprogress",0)
                    for(document in it){

                        db.collection("users").document(document.id).set(items, SetOptions.merge())
                    }
                }
                Toast.makeText(this.context,"Berhasil mengubah target", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }else{
                Toast.makeText(this.context,"Tolong isi data dengan lengkap", Toast.LENGTH_SHORT).show()
            }

        }
        addDialog.setNegativeButton("Cancel"){
                dialog,_->
            dialog.dismiss()
        }
        addDialog.create()
        addDialog.show()

    }
    fun getOrderData(){
        historyOrderArrayList.clear()
        db = FirebaseFirestore.getInstance()
        db.collection("orders").whereEqualTo("sales_id",uidRecylcer).get()
            .addOnCompleteListener {
                for(dc : DocumentChange in it.result.documentChanges!!){
                    if(dc.type == DocumentChange.Type.ADDED){
                        historyOrderArrayList.add(dc.document.toObject(Orders::class.java))
                    }
                }
                if(it.isComplete){
                    for(item in historyOrderArrayList){
                        var x = item.tanggal?.split("/")
                        for(i in 0..dataTotalOrderBulanan.size-1){
                           if(i==x!![1].toInt()-1){
                               dataTotalOrderBulanan[i]++
                               dataNWOrderBulanan[i]+=item.total_harga!!.toFloat()
                           }
                        }

                    }
                    setBarchartData()
                    setLinechartData()
                }
            }

    }

    fun setBarchartData(){
        val xvalues = ArrayList<String>()
        xvalues.add("Jan")
        xvalues.add("Feb")
        xvalues.add("Mar")
        xvalues.add("Apr")
        xvalues.add("Mei")
        xvalues.add("Jun")
        xvalues.add("Jul")
        xvalues.add("Agust")
        xvalues.add("Sept")
        xvalues.add("Octo")
        xvalues.add("Nov")
        xvalues.add("Dec")

        var barEntries = ArrayList<BarEntry>()

        for(i in 0..dataTotalOrderBulanan.size-1){
            barEntries.add(BarEntry(i.toFloat(),dataTotalOrderBulanan[i].toFloat(),"halo"))
        }

        val bardataset =  BarDataSet(barEntries,"total pembelian")
        bardataset.setColors(*ColorTemplate.COLORFUL_COLORS)
        bardataset.valueTextSize=10f
        val data = BarData(bardataset)

        binding.barChart.data=data
        binding.barChart.minimumHeight=700
        binding.barChart.minimumWidth=1000
        var xAxis = binding.barChart.xAxis
        xAxis.valueFormatter= IndexAxisValueFormatter(xvalues)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.setLabelCount(xvalues.size)
        binding.barChart.animateY(2000)
    }

    fun setLinechartData(){
        val xvalues = ArrayList<String>()
        xvalues.add("Jan")
        xvalues.add("Feb")
        xvalues.add("Mar")
        xvalues.add("Apr")
        xvalues.add("Mei")
        xvalues.add("Jun")
        xvalues.add("Jul")
        xvalues.add("Agust")
        xvalues.add("Sept")
        xvalues.add("Octo")
        xvalues.add("Nov")
        xvalues.add("Dec")

        var LEntries = ArrayList<Entry>()

        for(i in 0..dataTotalOrderBulanan.size-1){
            LEntries.add(Entry(i.toFloat(),dataNWOrderBulanan[i]))
        }

        val LineDatas =  LineDataSet(LEntries,"Total pembelian Semuanya")

        val data = LineData(LineDatas)

        binding.linechart.data=data
        binding.linechart.minimumHeight=700
        binding.linechart.minimumWidth=2000
        var xAxis = binding.linechart.xAxis
        xAxis.valueFormatter= IndexAxisValueFormatter(xvalues)
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(false)
        xAxis.setLabelCount(xvalues.size,true)
        LineDatas.setColors(*ColorTemplate.JOYFUL_COLORS)
        LineDatas.valueTextSize=20f
    }

}