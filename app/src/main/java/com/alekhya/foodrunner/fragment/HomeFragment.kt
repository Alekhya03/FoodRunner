package com.alekhya.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.alekhya.foodrunner.R
import com.alekhya.foodrunner.adapter.DashboardRecyclerAdapter
import com.alekhya.foodrunner.model.Restaurant
import com.alekhya.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.sort_radiogroup.view.*
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap


class HomeFragment(val Hcontext: Context) : Fragment() {
    lateinit var recyclerDashboard : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var dashboardRecyclerAdapter: DashboardRecyclerAdapter
    lateinit var radioButtonView:View
    lateinit var progressBar: ProgressBar
    lateinit var prgresslayout :RelativeLayout
    var restInfoList= arrayListOf<Restaurant>()



    var ratingComparator= Comparator<Restaurant> { restaurant1, restaurant2 ->

        if(restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true)==0){
            restaurant1.restaurantName.compareTo(restaurant2.restaurantName,true)
        }
        else{
            restaurant1.restaurantRating.compareTo(restaurant2.restaurantRating,true)
        }
    }

    var costComparator= Comparator<Restaurant> { restaurant1, restaurant2 ->
        restaurant1.costperperson.compareTo(restaurant2.costperperson,true)
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerDashboard=view.findViewById(R.id.recyclerDashboard)
        layoutManager=LinearLayoutManager(activity)
        prgresslayout=view.findViewById(R.id.progresslayout)
        progressBar=view.findViewById(R.id.progressBar)

        progressBar.visibility=View.VISIBLE
        val queue= Volley.newRequestQueue(activity as Context)
        val url ="http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                Response.Listener {
                    try {
                        progressBar.visibility=View.INVISIBLE
                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")
                        if (success) {
                            val data = responseJsonObjectData.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantJsonObject = data.getJSONObject(i)
                                val restaurantObject = Restaurant(
                                    restaurantJsonObject.getString("id"),
                                    restaurantJsonObject.getString("name"),
                                    restaurantJsonObject.getString("rating"),
                                    restaurantJsonObject.getString("cost_for_one"),
                                    restaurantJsonObject.getString("image_url")
                                )
                                restInfoList.add(restaurantObject)

                                dashboardRecyclerAdapter =
                                    DashboardRecyclerAdapter(activity as Context, restInfoList)

                                recyclerDashboard.adapter = dashboardRecyclerAdapter
                                recyclerDashboard.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(context, "Some Error Occured ", Toast.LENGTH_LONG).show()
                        }
                    }
                    catch (e:JSONException)
                    {
                        Toast.makeText(context, "Some Eexception Occured ", Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener {

                    Toast.makeText(context, "Please Try again ", Toast.LENGTH_LONG).show()

                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "20d7484e26c9b7"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)

        }
        else{
            val dialog=androidx.appcompat.app.AlertDialog.Builder(activity as Context)
            dialog.setTitle("No Internet")
            dialog.setMessage("Internet Connection can't be establish!")
            dialog.setPositiveButton("Open Settings"){ text, listener->
                val settingsIntent= Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit"){ text, listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.setCancelable(false)

            dialog.create()
            dialog.show()
            
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id=item.itemId

        when(id){

            R.id.sortOptions->{
                radioButtonView= View.inflate(Hcontext,R.layout.sort_radiogroup,null)
                androidx.appcompat.app.AlertDialog.Builder(activity as Context)
                    .setTitle("Sort By :")
                    .setView(radioButtonView)
                    .setPositiveButton("OK") { text, listener ->
                        if (radioButtonView.radio_high_to_low.isChecked) {
                            Collections.sort(restInfoList, costComparator)
                            restInfoList.reverse()
                            dashboardRecyclerAdapter.notifyDataSetChanged()
                        }
                        if (radioButtonView.radio_low_to_high.isChecked) {
                            Collections.sort(restInfoList, costComparator)
                            dashboardRecyclerAdapter.notifyDataSetChanged()
                        }
                        if (radioButtonView.radio_rating.isChecked) {
                            Collections.sort(restInfoList, ratingComparator)
                            restInfoList.reverse()
                            dashboardRecyclerAdapter.notifyDataSetChanged()
                        }
                    }
                    .setNegativeButton("CANCEL") { text, listener ->

                    }
                    .create()
                    .show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}