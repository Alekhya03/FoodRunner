package com.alekhya.foodrunner.activity

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import com.android.volley.Request
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alekhya.foodrunner.R
import com.alekhya.foodrunner.adapter.RestaurantMenuRecyclerAdapter
import com.alekhya.foodrunner.model.RestaurantMenu
import com.alekhya.foodrunner.util.ConnectionManager
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException

class RestaurantMenuActivity : AppCompatActivity() {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar


    lateinit var recyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var rmenuAdapter: RestaurantMenuRecyclerAdapter
    lateinit var progresslayout: RelativeLayout
    lateinit var proceedToCartLayout: RelativeLayout
    lateinit var buttonProceedToCart: Button

    var restaurantMenuList = arrayListOf<RestaurantMenu>()
    lateinit var restId: String
    lateinit var restname: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_menu)
        toolbar = findViewById(R.id.restaurant_menu_toolBar)
        proceedToCartLayout = findViewById(R.id.ProceedToCartLayout)
        buttonProceedToCart = findViewById(R.id.ProceedCartBtn)
        progresslayout = findViewById(R.id.restaurant_menu_ProgressLayout)
        layoutManager = LinearLayoutManager(this)
        recyclerView = findViewById(R.id.recyclerViewRestaurantMenu)

        progresslayout.visibility = View.VISIBLE

        restId = intent.getStringExtra("id")
        restname = intent.getStringExtra("restaurantName")

        setUpToolbar()

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restId"


        if (ConnectionManager().checkConnectivity(this)) {
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                url, null,

                Response.Listener {
                    try {

                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")
                        if (success) {
                            progresslayout.visibility = View.INVISIBLE
                            val data = responseJsonObjectData.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restaurantJmenusonObject = data.getJSONObject(i)
                                val restaurantMenuObject = RestaurantMenu(
                                    restaurantJmenusonObject.getString("id"),
                                    restaurantJmenusonObject.getString("name"),
                                    restaurantJmenusonObject.getString("cost_for_one")
                                )
                                restaurantMenuList.add(restaurantMenuObject)

                                rmenuAdapter = RestaurantMenuRecyclerAdapter(
                                    this,
                                    restaurantMenuList,
                                    proceedToCartLayout,
                                    buttonProceedToCart,
                                    restId,
                                    restname
                                )

                                recyclerView.adapter = rmenuAdapter
                                recyclerView.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(this, "Some Error Occured ", Toast.LENGTH_LONG).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(this, "Some Eexception Occured ", Toast.LENGTH_LONG).show()
                    }
                },
                Response.ErrorListener {

                    Toast.makeText(this, "Please Try again ", Toast.LENGTH_LONG).show()

                }) {

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "20d7484e26c9b7"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)

        } else {
            val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            dialog.setTitle("No Internet")
            dialog.setMessage("Internet Connection can't be establish!")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(settingsIntent)

            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.setCancelable(false)

            dialog.create()
            dialog.show()

        }
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = restname
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        when (id) {
            android.R.id.home -> {
                if (rmenuAdapter.getSelectedItemCount() > 0) {

                    val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                    alterDialog.setTitle("Alert!")
                    alterDialog.setMessage("Going back will remove everything from cart")
                    alterDialog.setPositiveButton("Okay") { text, listener ->
                        super.onBackPressed()
                    }
                    alterDialog.setNegativeButton("No") { text, listener ->

                    }
                    alterDialog.show()
                } else {
                    super.onBackPressed()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (rmenuAdapter.getSelectedItemCount() > 0) {

            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay") { text, listener ->
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("No") { text, listener ->
            }
            alterDialog.show()
        } else {
            super.onBackPressed()
        }
    }
}