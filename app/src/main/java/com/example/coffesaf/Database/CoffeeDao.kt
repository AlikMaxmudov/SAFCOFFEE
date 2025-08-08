package com.example.coffesaf.Database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CoffeeDao {

    @Query("SELECT * FROM coffee_items")
    suspend fun getAll(): List<CoffeeEntity>

    @Query("SELECT * FROM coffee_items WHERE category = :category")
    suspend fun getCoffeeByCategory(category: String): List<CoffeeEntity>

    @Query("SELECT DISTINCT category FROM coffee_items")
    suspend fun getAllCategories(): List<String>

    @Query("SELECT * FROM coffee_items WHERE id = :id")
    suspend fun getCoffeeById(id: Int): CoffeeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(coffee: CoffeeEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coffees: List<CoffeeEntity>)

}