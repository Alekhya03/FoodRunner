package com.alekhya.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.alekhya.foodrunner.R
import com.alekhya.foodrunner.adapter.FavouritesRecyclerAdapter
import com.alekhya.foodrunner.database.RestaurantDatabase
import com.alekhya.foodrunner.database.RestaurantEntity

class FavoritesFragment : Fragment() {


    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var favouriteAdapter: FavouritesRecyclerAdapter
    lateinit var noFav:RelativeLayout

    var favRestaurantInfoList= listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorites, container, false)
        layoutManager = LinearLayoutManager(activity)
        recyclerView = view.findViewById(R.id.recyclerViewFavouriteRestaurant)
        noFav=view.findViewById(R.id.no_favourites)

        favRestaurantInfoList=RetrieveFavourites(activity as Context).execute().get()

        if(activity!=null)
        {
            if(favRestaurantInfoList.isEmpty())
            {
                noFav.visibility=View.VISIBLE
            }
            favouriteAdapter = FavouritesRecyclerAdapter(activity as Context, favRestaurantInfoList)
            recyclerView.adapter = favouriteAdapter
            recyclerView.layoutManager = layoutManager
        }

        return view

    }

    class RetrieveFavourites(val context: Context) :AsyncTask<Void,Void,List<RestaurantEntity>>()
    {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db= Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

            return db.restaurantDao().getAllRestaurants()
        }

    }

}