package com.kirtik.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kirtik.foodrunner.R
import com.kirtik.foodrunner.database.FoodEntity
import kotlinx.android.synthetic.main.recycler_resdetail_single_row.*

class CartRecyclerAdapter (val context: Context,val dbFoodList : List<FoodEntity>) : RecyclerView.Adapter <CartRecyclerAdapter.CartViewHolder>() {



    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val txtFoodItemName : TextView = view.findViewById(R.id.txtFoodItemName)
        val txtFoodItemPrice : TextView = view.findViewById(R.id.txtFoodItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row,parent,false)


        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dbFoodList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = dbFoodList[position]

        holder.txtFoodItemName.text = item.foodName
        holder.txtFoodItemPrice.text = "Rs."+item.foodPrice




    }


}