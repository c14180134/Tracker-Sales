package com.example.trackersales.room

import androidx.room.*

@Dao
interface LocationDao {

    @Insert
    fun addLoc(location:Location)

    @Query("SELECT * FROM location ORDER BY id DESC")
    fun getLocation() : List<Location>

    @Query("SELECT * FROM location WHERE id=:location_id")
    fun getLocation(location_id: Int) : List<Location>

    @Update
    fun updateLocation(location:Location)

    @Query("DELETE FROM location")
    fun nukeTable()

    @Delete
    fun deleteLocation(location:Location)

}