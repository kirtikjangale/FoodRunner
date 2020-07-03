package com.kirtik.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kirtik.foodrunner.R
import com.kirtik.foodrunner.model.OrderItem

class OrderHistorySecondaryRecyclerAdapter(val context:Context,val foodList:List<OrderItem>) : RecyclerView.Adapter<OrderHistorySecondaryRecyclerAdapter.OrderHistorySecondaryViewHolder>(){

    class OrderHistorySecondaryViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val resName:TextView = view.findViewById(R.id.txtFoodItemName)
        val foodPrice : TextView = view.findViewById(R.id.txtFoodItemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistorySecondaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_items_single,parent,false)
        return OrderHistorySecondaryViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  foodList.size
    }

    override fun onBindViewHolder(holder: OrderHistorySecondaryViewHolder, position: Int) {
        val item = foodList[position]

        holder.resName.text = item.foodName
        holder.foodPrice.text = "Rs."+item.foodPrice
    }
}