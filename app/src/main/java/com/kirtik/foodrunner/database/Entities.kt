package com.kirtik.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="foodItems")
data class FoodEntity (
    @PrimaryKey val food_id:String,
    @ColumnInfo(name="food_name") val foodName:String,
    @ColumnInfo(name="food_price") val foodPrice:String

)

@Entity(tableName = "resDetails")
data class Restaurant(
    @PrimaryKey val res_id:String,
    @ColumnInfo(name="res_name") val resName:String,
    @ColumnInfo(name="res_rating") val resRating:String,
    @ColumnInfo(name="res_cost") val resCost:String,
    @ColumnInfo(name="res_image") val resImage:String
)