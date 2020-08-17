package com.alekhya.foodrunner.adapter


import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.alekhya.foodrunner.R
import com.alekhya.foodrunner.model.CartItem

class CartAdapter(val context: Context, private val cartItems:ArrayList<CartItem>): RecyclerView.Adapter<CartAdapter.ViewHolderCart>() {


    class ViewHolderCart(view: View): RecyclerView.ViewHolder(view){
        val textViewOrderItem: TextView =view.findViewById(R.id.orderedItem)
        val textViewOrderItemPrice: TextView =view.findViewById(R.id.orderedItemPrice)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCart {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.cart_item_single_row,parent,false)
        return ViewHolderCart(view)
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderCart, position: Int) {
        val cartItemObject=cartItems[position]

        holder.textViewOrderItem.text=cartItemObject.itemName
        holder.textViewOrderItemPrice.text="Rs. "+cartItemObject.itemPrice
    }


}