package com.alekhya.foodrunner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.alekhya.foodrunner.R
import com.alekhya.foodrunner.activity.RestaurantMenuActivity
import com.alekhya.foodrunner.database.RestaurantDatabase
import com.alekhya.foodrunner.database.RestaurantEntity
import com.alekhya.foodrunner.model.Restaurant
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context: Context, private val itemList: ArrayList<Restaurant>) :
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val restName: TextView = view.findViewById(R.id.restaurantNameView)
        val restRating: TextView = view.findViewById(R.id.textViewRating)
        val costperperson: TextView = view.findViewById(R.id.pricePerPerson)
        val restImage: ImageView = view.findViewById(R.id.restaurantImage)
        val favourites: TextView = view.findViewById(R.id.viewFavourite)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_single_row, parent, false)
        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {

        val restaurant = itemList[position]
        holder.restName.text = restaurant.restaurantName
        holder.restRating.text = restaurant.restaurantRating
        holder.costperperson.text = restaurant.costperperson + "/Person"
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.ic_action_resaurent)
            .into(holder.restImage)

        holder.llContent.setOnClickListener {
            val intent = Intent(context, RestaurantMenuActivity::class.java)
            intent.putExtra("id", restaurant.restaurantId)
            intent.putExtra("restaurantName", holder.restName.text.toString())
            context.startActivity(intent)
        }

        val restaurantEntity = RestaurantEntity(
            restaurant.restaurantId,
            restaurant.restaurantName,
            restaurant.restaurantRating,
            restaurant.costperperson,
            restaurant.restaurantImage
        )

        holder.favourites.setOnClickListener(View.OnClickListener {
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {

                val result = DBAsyncTask(context, restaurantEntity, 2).execute().get()

                if (result) {

                    Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()

                    holder.favourites.setTag("liked")//new value
                    holder.favourites.background =
                        context.resources.getDrawable(R.drawable.ic_action_favourites)
                } else {
                    Toast.makeText(context, "Some error occured", Toast.LENGTH_SHORT).show()
                }

            } else {

                val result = DBAsyncTask(context, restaurantEntity, 3).execute().get()
                if (result) {

                    Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show()

                    holder.favourites.tag = "unlike"
                    holder.favourites.background =
                        context.resources.getDrawable(R.drawable.ic_action_favouritesoutlined)
                } else {

                    Toast.makeText(context, "Some error occurred", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    class DBAsyncTask(
        val context: Context,
        private val restaurantEntity: RestaurantEntity,
        private val mode: Int
    ) : AsyncTask<Void, Void, Boolean>() {

        private val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {


            when (mode) {
                1 -> {
                    val restaurant: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurantId)
                    db.close()
                    return restaurant != null
                }
                2 -> {
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                else -> return false
            }
        }
    }


}