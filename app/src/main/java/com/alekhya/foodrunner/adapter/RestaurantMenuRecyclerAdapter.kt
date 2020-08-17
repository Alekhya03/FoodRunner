package com.alekhya.foodrunner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alekhya.foodrunner.R
import com.alekhya.foodrunner.activity.CartActivity
import com.alekhya.foodrunner.model.RestaurantMenu

class RestaurantMenuRecyclerAdapter(val context : Context, private val restaurantFoodItemList: ArrayList<RestaurantMenu>, val proceedToCartPassed:RelativeLayout, val buttonProceedToCart:Button, val restaurantId:String, val restaurantName:String):RecyclerView.Adapter<RestaurantMenuRecyclerAdapter.RestaurantMenuViewHolder>() {

    var itemSelectedCount:Int=0
    lateinit var proceedToCart: RelativeLayout
    var itemsSelectedId= arrayListOf<String>()


    class RestaurantMenuViewHolder(view:View):RecyclerView.ViewHolder(view) {
            val foodNumber:TextView=view.findViewById(R.id.foodSerialNumber)
            val foodName: TextView=view.findViewById(R.id.ItemNametextview)
            val foodPrice: TextView=view.findViewById(R.id.ItemPricetextview)
            val addToCartBtn: Button =view.findViewById(R.id.buttonAddToCart)
    }

    fun getSelectedItemCount():Int{
        return itemSelectedCount
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantMenuViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurant_menu_single_row,parent,false)
        return RestaurantMenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restaurantFoodItemList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RestaurantMenuViewHolder, position: Int) {

        val restaurantFoodItem = restaurantFoodItemList[position]

        holder.addToCartBtn.setTag(restaurantFoodItem.id + "")//save the item id in textViewName Tag ,will be used to add to cart
        holder.foodNumber.text = (position + 1).toString()//position starts from 0
        holder.foodName.text = restaurantFoodItem.foodname
        holder.foodPrice.text = "Rs." + restaurantFoodItem.cost_for_one


        proceedToCart = proceedToCartPassed
        holder.addToCartBtn.setOnClickListener(View.OnClickListener {

            if(holder.addToCartBtn.text.toString() == "Remove")
            {
                    itemSelectedCount--
                    itemsSelectedId.remove(holder.addToCartBtn.getTag().toString())
                    holder.addToCartBtn.text = "Add"
                    holder.addToCartBtn.setBackgroundColor(Color.rgb(244, 67, 54))

            }
            else
            {
                    itemSelectedCount++
                    itemsSelectedId.add(holder.addToCartBtn.getTag().toString())
                    holder.addToCartBtn.text = "Remove"
                    holder.addToCartBtn.setBackgroundColor(Color.rgb(255, 196, 0))
            }


            if (itemSelectedCount > 0) {
                proceedToCart.visibility = View.VISIBLE
            } else {
                proceedToCart.visibility = View.INVISIBLE
            }
        })

        buttonProceedToCart.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra("restaurantId",restaurantId.toString())
            intent.putExtra("restaurantName",restaurantName)
            intent.putExtra("selectedItemsId",itemsSelectedId)
            context.startActivity(intent)
        })

    }

}