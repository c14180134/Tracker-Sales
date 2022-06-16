package com.example.trackersales.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Location::class],
    version = 2
)

abstract class LocationDB : RoomDatabase(){

    abstract fun LocationDao() : LocationDao

    companion object {

        @Volatile private var instance : LocationDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            LocationDB::class.java,
            "locationhistory3.db"
        ).build()

    }

}