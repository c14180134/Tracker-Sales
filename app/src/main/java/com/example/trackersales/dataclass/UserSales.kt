package com.example.trackersales.dataclass

data class UserSales(var email: String ?= null, var notlp : String ?= null , var long : Long?=null ,
                     var lat : Long?= null , var UID : String ?= null,var tanggalbergabung:String?=null,
                     var admin:Boolean?=null, var currentprogress:Long?=null, var tanggalprogress:String?=null,
                     var timeUpdate: String?=null,var todaysold:Int?=null,var speed:String?=null,
                     var target:Long?=null, var tokenFCM:String?=null)
