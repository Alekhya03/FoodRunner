package com.alekhya.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.alekhya.foodrunner.R
import com.alekhya.foodrunner.adapter.OrderHistoryAdapter
import com.alekhya.foodrunner.model.OrderHistory
import com.alekhya.foodrunner.util.ConnectionManager
import org.json.JSONException


class OrderFragment : Fragment() {
    lateinit var layoutManagerHistory: RecyclerView.LayoutManager
    lateinit var menuAdapter1: OrderHistoryAdapter
    lateinit var recyclerViewOrders: RecyclerView
    lateinit var orderHistoryProgressDialog :RelativeLayout
    lateinit var fragmentNoOrders: RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_order, container, false)
        recyclerViewOrders=view.findViewById(R.id.recyclerViewAllOrders)
        orderHistoryProgressDialog=view.findViewById(R.id.order_activity_history_Progressdialog)
        fragmentNoOrders=view.findViewById(R.id.order_history_fragment_no_orders)

        layoutManagerHistory= LinearLayoutManager(activity as Context)

        val orderedRestaurantList=ArrayList<OrderHistory>()

        val sharedPreferencess= context?.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        val user_id= sharedPreferencess?.getString("user_id","000")
        if (ConnectionManager().checkConnectivity(activity as Context)) {

            orderHistoryProgressDialog.visibility=View.VISIBLE

            try {

                val queue = Volley.newRequestQueue(activity as Context)
                val url ="http://13.235.250.119/v2/orders/fetch_result/$user_id"
                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")
                        if (success) {

                            val data = responseJsonObjectData.getJSONArray("data")

                            if(data.length()==0){

                                Toast.makeText(context, "No Orders Placed yet", Toast.LENGTH_SHORT).show()

                                fragmentNoOrders.visibility=View.VISIBLE

                            }else
                            {
                                fragmentNoOrders.visibility=View.INVISIBLE

                                for (i in 0 until data.length()) {
                                    val restaurantItemJsonObject = data.getJSONObject(i)

                                    val eachRestaurantObject = OrderHistory(
                                        restaurantItemJsonObject.getString("order_id"),
                                        restaurantItemJsonObject.getString("restaurant_name"),
                                        restaurantItemJsonObject.getString("total_cost"),
                                        restaurantItemJsonObject.getString("order_placed_at").substring(0,10)
                                    )
                                    orderedRestaurantList.add(eachRestaurantObject)

                                    menuAdapter1 = OrderHistoryAdapter(activity as Context, orderedRestaurantList)
                                    recyclerViewOrders.adapter = menuAdapter1
                                    recyclerViewOrders.layoutManager = layoutManagerHistory

                                }

                            }

                        }
                        orderHistoryProgressDialog.visibility=View.INVISIBLE
                    },
                    Response.ErrorListener {
                        orderHistoryProgressDialog.visibility=View.INVISIBLE

                        Toast.makeText(
                            context,
                            "Some Error occurred!!!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        headers["Content-type"] = "application/json"
                        headers["token"] = "20d7484e26c9b7"

                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) { Toast.makeText(context, "Some Unexpected error occurred!", Toast.LENGTH_SHORT).show()
            }

        } else {
            val alterDialog= AlertDialog.Builder(activity as Context)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIFI_SETTINGS)//open wifi settings
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()
        }
        return view
    }

}