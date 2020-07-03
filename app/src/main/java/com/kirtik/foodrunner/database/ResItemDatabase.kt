package com.kirtik.foodrunner.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Restaurant::class],version = 1)
abstract class ResItemDatabase : RoomDatabase() {

    abstract fun resDao() : ResDao

}