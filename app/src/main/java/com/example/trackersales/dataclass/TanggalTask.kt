package com.example.trackersales.dataclass

import com.google.firebase.Timestamp
import java.util.*
import kotlin.collections.ArrayList

data class TanggalTask(var tanggal:String?=null, var listTask:ArrayList<IsiTask>?=null,var uid:String?=null, var done:Boolean?=null,var dateTime: Date?=null)
