package com.alekhya.foodrunner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restaurants")
data class RestaurantEntity(
    @ColumnInfo(name="restaurant_id") @PrimaryKey var restaurantId:String,
    @ColumnInfo(name="restaurant_name") var restaurantName:String,
    @ColumnInfo(name="restaurant_rating") var restaurentRating: String,
    @ColumnInfo(name="restaurant_cost") var costPerPerson: String,
    @ColumnInfo(name="restaurant_image")  var restaurantImage:String
)