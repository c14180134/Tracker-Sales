package com.example.trackersales.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.trackersales.R
import com.example.trackersales.dataclass.Laporan_Check_in
import com.example.trackersales.dataclass.UserCustomer
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class AdapterLaporan(private val listLaporan:ArrayList<Laporan_Check_in>):RecyclerView.Adapter<AdapterLaporan.RecLaporanViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecLaporanViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_laporan,parent,false)
        return RecLaporanViewHolder(itemView)
    }

    class RecLaporanViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        val Judul : TextView = itemView.findViewById(R.id.judulLaporan)
        val tanggal : TextView = itemView.findViewById(R.id.tanggalTv)
        val holderLaporan: CardView = itemView.findViewById(R.id.holderLaporan)
    }

    override fun onBindViewHolder(holder: RecLaporanViewHolder, position: Int) {
        val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/M/yyyy")
        val x = listLaporan.sortedByDescending { LocalDate.parse(it.tanggal, dateTimeFormatter) }
        val laporan: Laporan_Check_in = x[position]

        holder.Judul.text = laporan.judul
        holder.tanggal.text = laporan.tanggal
        holder.holderLaporan.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("sales_id",laporan.sales_id)
            bundle.putString("tanggal",laporan.tanggal)
            bundle.putString("catatan",laporan.catatan)
            bundle.putString("uniqueid",laporan.uniqueid)
            laporan.long?.let { bundle.putDouble("long", it) }
            laporan.lat?.let { bundle.putDouble("lat", it) }
            bundle.putString("judul",laporan.judul)
            it.findNavController().navigate(R.id.detailLaporan,bundle)
        }
    }

    override fun getItemCount(): Int {
        return listLaporan.size
    }
}