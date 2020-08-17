package com.alekhya.foodrunner.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.alekhya.foodrunner.R
import com.alekhya.foodrunner.adapter.CartAdapter
import com.alekhya.foodrunner.model.CartItem
import com.alekhya.foodrunner.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var textViewOrderingFrom: TextView
    lateinit var buttonPlaceOrder: Button
    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var menuAdapter: CartAdapter
    lateinit var restaurantId: String
    lateinit var restaurantName: String
    lateinit var linearLayout: LinearLayout
    lateinit var activityCartProgressLayout: RelativeLayout
    lateinit var selectedItemsId: ArrayList<String>
    var totalAmount = 0
    var cartListItems = arrayListOf<CartItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        buttonPlaceOrder = findViewById(R.id.buttonPlaceOrder)
        textViewOrderingFrom = findViewById(R.id.OrderingFrom)
        linearLayout = findViewById(R.id.linearLayout)
        toolbar = findViewById(R.id.toolBar)
        activityCartProgressLayout = findViewById(R.id.activity_cart_Progressdialog)

        restaurantId = intent.getStringExtra("restaurantId")
        restaurantName = intent.getStringExtra("restaurantName")
        selectedItemsId = intent.getStringArrayListExtra("selectedItemsId")

        textViewOrderingFrom.text = restaurantName

        setToolBar()
        layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.recyclerViewCart)

        fetchData()


        buttonPlaceOrder.setOnClickListener(View.OnClickListener {

            val sharedPreferencess = this.getSharedPreferences(
                getString(R.string.shared_preferences),
                Context.MODE_PRIVATE
            )

            if (ConnectionManager().checkConnectivity(this)) {

                activityCartProgressLayout.visibility = View.VISIBLE

                try {
                    val foodJsonArray = JSONArray()

                    for (foodItem in selectedItemsId) {
                        val singleItemObject = JSONObject()
                        singleItemObject.put("food_item_id", foodItem)
                        foodJsonArray.put(singleItemObject)
                    }

                    val sendOrder = JSONObject()

                    sendOrder.put("user_id", sharedPreferencess.getString("user_id", "0"))
                    sendOrder.put("restaurant_id", restaurantId.toString())
                    sendOrder.put("total_cost", totalAmount)
                    sendOrder.put("food", foodJsonArray)

                    val queue = Volley.newRequestQueue(this)

                    val url = "http://13.235.250.119/v2/place_order/fetch_result/"

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        sendOrder,
                        Response.Listener {

                            val responseJsonObjectData = it.getJSONObject("data")

                            val success = responseJsonObjectData.getBoolean("success")

                            if (success) {


                                val intent = Intent(this, OrderSuccessActivity::class.java)
                                startActivity(intent)
                                finishAffinity()


                            } else {
                                val responseMessageServer =
                                    responseJsonObjectData.getString("errorMessage")
                                Toast.makeText(
                                    this,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                            activityCartProgressLayout.visibility = View.INVISIBLE
                        },
                        Response.ErrorListener {

                            println("ssssss" + it)

                            Toast.makeText(
                                this,
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
                    jsonObjectRequest.setRetryPolicy(
                        DefaultRetryPolicy(
                            15000, 1,
                            1f
                        )
                    )

                    queue.add(jsonObjectRequest)

                } catch (e: JSONException) {

                    Toast.makeText(this, "Some unexpected error occurred!", Toast.LENGTH_SHORT)
                        .show()
                }

            } else {

                val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                alterDialog.setTitle("No Internet")
                alterDialog.setMessage("Internet Connection can't be establish!")
                alterDialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                    startActivity(settingsIntent)
                }

                alterDialog.setNegativeButton("Exit") { text, listener ->
                    finishAffinity()
                }
                alterDialog.setCancelable(false)

                alterDialog.create()
                alterDialog.show()
            }
        })


    }

    fun fetchData() {

        if (ConnectionManager().checkConnectivity(this)) {

            activityCartProgressLayout.visibility = View.VISIBLE

            try {

                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

                val jsonObjectRequest = @SuppressLint("SetTextI18n")
                object : JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")
                        if (success) {
                            val data = responseJsonObjectData.getJSONArray("data")
                            cartListItems.clear()
                            totalAmount = 0

                            for (i in 0 until data.length()) {
                                val cartItemJsonObject = data.getJSONObject(i)

                                if (selectedItemsId.contains(cartItemJsonObject.getString("id"))) {

                                    val menuObject = CartItem(
                                        cartItemJsonObject.getString("id"),
                                        cartItemJsonObject.getString("name"),
                                        cartItemJsonObject.getString("cost_for_one"),
                                        cartItemJsonObject.getString("restaurant_id")
                                    )

                                    totalAmount += cartItemJsonObject.getString("cost_for_one")
                                        .toString().toInt()
                                    cartListItems.add(menuObject)

                                }

                                menuAdapter = CartAdapter(this, cartListItems)
                                recyclerView.adapter = menuAdapter
                                recyclerView.layoutManager = layoutManager

                            }
                            buttonPlaceOrder.text = "Place Order(Total:Rs. $totalAmount)"
                        }
                        activityCartProgressLayout.visibility = View.INVISIBLE
                    },
                    Response.ErrorListener {
                        Toast.makeText(this, "Some Error occurred!!!", Toast.LENGTH_SHORT).show()
                        activityCartProgressLayout.visibility = View.INVISIBLE

                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        headers["Content-type"] = "application/json"
                        headers["token"] = "20d7484e26c9b7"

                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException) {
                Toast.makeText(this, "Some Unexpected error occurred!", Toast.LENGTH_SHORT).show()
            }

        } else {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                startActivity(settingsIntent)
            }

            alterDialog.setNegativeButton("Exit") { text, listener ->
                finishAffinity()//closes all the instances of the app and the app closes completely
            }
            alterDialog.setCancelable(false)

            alterDialog.create()
            alterDialog.show()

        }

    }

    fun setToolBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                super.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}