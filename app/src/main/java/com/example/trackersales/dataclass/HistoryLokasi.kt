package com.example.trackersales.dataclass

import com.example.trackersales.room.Location

data class HistoryLokasi(
    var Location:ArrayList<Location>?=null,
    var uid:String?=null,
    var tanggal:String?=null
)