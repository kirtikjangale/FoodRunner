package com.kirtik.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.kirtik.foodrunner.R
import com.kirtik.foodrunner.adapter.HomeRecyclerAdapter
import com.kirtik.foodrunner.adapter.OrderHistoryMainRecyclerAdapter
import com.kirtik.foodrunner.model.OrderItem
import com.kirtik.foodrunner.model.Orders
import com.kirtik.foodrunner.model.Restaurant
import com.kirtik.foodrunner.util.ConnectionManager
import org.json.JSONException
import java.lang.Exception


class OrderHistoryFragment : Fragment() {

    lateinit var recyclerOrderHistory : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryMainRecyclerAdapter
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        sharedPreferences = activity!!.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        recyclerOrderHistory = view.findViewById(R.id.recyclerOrderMain)
        layoutManager = LinearLayoutManager(activity)

        if(ConnectionManager().checkConnectivity(activity as Context)){
            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/orders/fetch_result/"+sharedPreferences.getString("user_id","-1").toString()

            println("url : $url")

            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET,url,null, Response.Listener {
                try{
                    val success =  it.getJSONObject("data").getBoolean("success")

                    if(success){
                        val data = it.getJSONObject("data").getJSONArray("data")


                        var orderList = arrayListOf<Orders>()

                        for (i in 0 until data.length()){

                            var foodList = arrayListOf<OrderItem>()

                            var foodItems = data.getJSONObject(i).getJSONArray("food_items")

                            for(j in 0 until foodItems.length()){
                                var name = foodItems.getJSONObject(j).getString("name")
                                var cost = foodItems.getJSONObject(j).getString("cost")
                                foodList.add(OrderItem(name,cost))
                            }

                            val obj = Orders(
                                data.getJSONObject(i).getString("restaurant_name"),
                                data.getJSONObject(i).getString("order_placed_at"),
                                foodList
                            )

                            orderList.add(obj)
                        }

                        recyclerAdapter = OrderHistoryMainRecyclerAdapter(activity as Context,orderList)
                        recyclerOrderHistory.adapter = recyclerAdapter
                        recyclerOrderHistory.layoutManager = layoutManager

                        println(orderList)

                    }
                    else{
                        Toast.makeText(activity,"Request response is false",Toast.LENGTH_LONG).show()
                    }
                }
                catch (e: Exception){
                    Toast.makeText(activity,"$e",Toast.LENGTH_LONG).show()
                }


            },
                Response.ErrorListener {
                    if(activity !=null){
                        Toast.makeText(activity as Context,"Volley", Toast.LENGTH_SHORT).show()
                    }
                }){

                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["Content-Type"]="application/json"
                    headers["token"] = "2192406a25aca8"
                    return headers
                }
            }

            queue.add(jsonObjectRequest)
        }
        else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){_,_->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Cancel"){ _,_->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }



        return view
    }

}
