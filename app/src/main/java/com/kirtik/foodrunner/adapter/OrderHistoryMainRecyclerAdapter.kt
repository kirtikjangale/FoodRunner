package com.kirtik.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kirtik.foodrunner.R
import com.kirtik.foodrunner.model.OrderItem
import com.kirtik.foodrunner.model.Orders

class OrderHistoryMainRecyclerAdapter(val context: Context,val orderList: List<Orders>) : RecyclerView.Adapter <OrderHistoryMainRecyclerAdapter.OrderHistoryMainViewHolder>(){








    class OrderHistoryMainViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val resName : TextView = view.findViewById(R.id.txtResName)
        val orderDate : TextView = view.findViewById(R.id.txtOrderDate)

        val recyclerView : RecyclerView = view.findViewById(R.id.recyclerOrderSecondary)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryMainViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_order_history_single,parent,false)
        return OrderHistoryMainViewHolder(view)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: OrderHistoryMainViewHolder, position: Int) {
        val order = orderList[position]

        holder.resName.text = order.resName
        holder.orderDate.text = order.orderDate


        var layoutManager = LinearLayoutManager(context)
        var recyclerAdapter : OrderHistorySecondaryRecyclerAdapter = OrderHistorySecondaryRecyclerAdapter(context,order.itemList)
        holder.recyclerView.adapter = recyclerAdapter
        holder.recyclerView.layoutManager = layoutManager




    }
}