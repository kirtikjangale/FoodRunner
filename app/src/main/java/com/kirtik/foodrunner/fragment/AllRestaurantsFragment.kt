package com.kirtik.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Adapter
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
import com.kirtik.foodrunner.model.Restaurant
import com.kirtik.foodrunner.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap


class AllRestaurantsFragment : Fragment() {


    lateinit var recyclerHome : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager

    val resList = arrayListOf<Restaurant>()

    lateinit var recyclerAdapter: HomeRecyclerAdapter

    val ratingComparator = kotlin.Comparator<Restaurant>{res1,res2->
        (if(res1.resRating.compareTo(res2.resRating,true)==0) {
            res1.resName.compareTo(res2.resName, true)
        } else{
            res1.resRating.compareTo(res2.resRating,true)
        })
    }
    val priceComparator = kotlin.Comparator<Restaurant>{res1,res2->
        (if(res1.resPrice.compareTo(res2.resPrice,true)==0) {
            res1.resName.compareTo(res2.resName, true)
        } else{
            res1.resPrice.compareTo(res2.resPrice,true)
        })
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_all_restaurants, container, false)

        setHasOptionsMenu(true)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)

        if(ConnectionManager().checkConnectivity(activity as Context)){
            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET,url,null, Response.Listener {

                try {
                    // progressLayout.visibility = View.GONE
                    val success = it.getJSONObject("data").getBoolean("success")
                    println(it)
                    if(success){
                        val data = it.getJSONObject("data").getJSONArray("data")

                        for (i in 0 until data.length()){
                            val resJsonObject = data.getJSONObject(i)

                            val resObject = Restaurant(
                                resJsonObject.getString("id"),
                                resJsonObject.getString("name"),
                                resJsonObject.getString("rating"),
                                resJsonObject.getString("cost_for_one"),
                                resJsonObject.getString("image_url")
                            )

                            resList.add(resObject)
                        }
                        recyclerAdapter = HomeRecyclerAdapter(activity as Context,resList)
                        recyclerHome.adapter = recyclerAdapter
                        recyclerHome.layoutManager = layoutManager


                    }
                    else{
                        Toast.makeText(activity as Context,"Some error occurred", Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e : JSONException){
                    Toast.makeText(activity as Context,"$e", Toast.LENGTH_SHORT).show()
                    println(e)
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
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home,menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == R.id.action_sort){

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Sort By?")


            val options = arrayOf("Price ( High to Low )","Price ( Low to High )","Rating")
            val checkedItem = 0

            var item :Int = 0
            builder.setSingleChoiceItems(options, checkedItem) { dialog, which ->
                item = which
            }


            builder.setPositiveButton("OK") { dialog, which ->

                if(item==0){
                    Collections.sort(resList,priceComparator)
                    resList.reverse()
                    recyclerAdapter.notifyDataSetChanged()
                }
                else if(item==1){
                    Collections.sort(resList,priceComparator)
                    recyclerAdapter.notifyDataSetChanged()
                }
                else{
                    Collections.sort(resList,ratingComparator)
                    resList.reverse()
                    recyclerAdapter.notifyDataSetChanged()
                }

            }
            builder.setNegativeButton("Cancel", null)


            val dialog = builder.create()
            dialog.show()

        }
        return super.onOptionsItemSelected(item)
    }


}
