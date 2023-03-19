package com.example.trackersales.dataclass

import android.os.Parcel
import android.os.Parcelable

data class Item( var harga:Long?=null,var nama:String?=null,var jumlah:Long?=null ):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readValue(Long::class.java.classLoader) as? Long
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(harga)
        parcel.writeString(nama)
        parcel.writeValue(jumlah)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}

fun getNama(){

    return
}