package com.kirtik.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResDao {

    @Insert
    fun insertFavRestaurant(restaurant: Restaurant)

    @Delete
    fun deleteFavRestaurant(restaurant: Restaurant)

    @Query("SELECT * FROM resDetails")
    fun getAllFavRestaurants() : List<Restaurant>

    @Query("SELECT * FROM resDetails where res_id=:resId")
    fun getResById(resId:String) : Restaurant

    @Query("DELETE FROM resDetails")
    fun deleteAll()
}