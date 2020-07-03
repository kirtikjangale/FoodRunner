package com.kirtik.foodrunner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.kirtik.foodrunner.model.FoodItem

@Dao
interface FoodDao {


    @Insert
    fun insertFoodItem(foodItem: FoodEntity)

    @Delete
    fun deleteFoodItem(foodItem: FoodEntity)

    @Query("SELECT * FROM foodItems")
    fun getAllFoodItems() : List<FoodEntity>

    @Query("DELETE  FROM foodItems")
    fun deleteAllFoodItems()


}