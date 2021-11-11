package com.example.practisespelling

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TextCard::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun textcardDao(): TextcardDao
}