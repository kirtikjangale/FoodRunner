package com.kirtik.foodrunner.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FoodEntity::class],version = 1)
abstract class FoodItemDatabase : RoomDatabase() {

    abstract fun foodDao(): FoodDao

}