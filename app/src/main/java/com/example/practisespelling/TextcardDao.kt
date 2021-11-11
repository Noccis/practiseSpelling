package com.example.practisespelling

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface  TextcardDao {

    @Insert
    fun insert(textCard: TextCard)

    @Delete
    fun delete(textCard: TextCard)

    @Query("SELECT * FROM textcard_table")
    fun getAll(): List<TextCard>

    @Query("SELECT * FROM textcard_table WHERE category LIKE :categoryName")
    fun findByCategory(categoryName: String): List<TextCard>

}