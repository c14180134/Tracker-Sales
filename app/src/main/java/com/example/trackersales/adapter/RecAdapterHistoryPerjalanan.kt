package com.example.trackersales.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.dataclass.HistoryLokasi
import com.example.trackersales.dataclass.UserSales
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RecAdapterHistoryPerjalanan(private val HistoryList: ArrayList<HistoryLokasi>) : RecyclerView.Adapter<RecAdapterHistoryPerjalanan.RecHistoryPerjalananViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecHistoryPerjalananViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_history_perjalanan,parent,false)
        return RecHistoryPerjalananViewHolder(itemView)
    }

    class RecHistoryPerjalananViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val tanggalHistory=itemView.findViewById<TextView>(R.id.tvTanggalHistoryPerjalanan)
        val itemHistory=itemView.findViewById<LinearLayout>(R.id.itemHistory)
    }

    override fun onBindViewHolder(
        holder: RecHistoryPerjalananViewHolder,
        position: Int
    ) {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/M/yyyy")
        val x = HistoryList.sortedByDescending { LocalDate.parse(it.tanggal, dateTimeFormatter) }
        val hsLokasi : HistoryLokasi = x[position]
        holder.tanggalHistory.text = hsLokasi.tanggal
        val bundle = Bundle()
        bundle.putParcelableArrayList("Lokasi", hsLokasi.Location)
        holder.itemHistory.setOnClickListener {
            it.findNavController().navigate(R.id.historyMap,bundle)
        }

    }

    override fun getItemCount(): Int {
        return HistoryList.size
    }
}