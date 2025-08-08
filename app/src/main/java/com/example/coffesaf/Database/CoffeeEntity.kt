package com.example.coffesaf.Database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "coffee_items")
data class CoffeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val description: String,
    val ingredients: String,
    val imageUri: String,
    val priceS: String,
    var priceM: String,
    val priceL: String,
    val rating: Float,
    val category: String
) : Parcelable