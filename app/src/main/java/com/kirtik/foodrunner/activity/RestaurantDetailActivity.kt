package com.kirtik.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kirtik.foodrunner.R
import com.kirtik.foodrunner.adapter.RestaurantDetailsRecyclerAdapter
import com.kirtik.foodrunner.model.FoodItem
import com.kirtik.foodrunner.util.ConnectionManager


class RestaurantDetailActivity : AppCompatActivity() {

    lateinit var recyclerResDetail : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var btnCart : Button
    lateinit var recyclerAdapter: RestaurantDetailsRecyclerAdapter
    lateinit var toolbar : Toolbar

    val foodItemList = arrayListOf<FoodItem>()

    var resId : String? = null
    var resName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant_detail)


        recyclerResDetail = findViewById(R.id.recyclerResDetail)
        toolbar = findViewById(R.id.toolbar)
        btnCart = findViewById(R.id.btnCart)
        layoutManager = LinearLayoutManager(this@RestaurantDetailActivity)




        if(intent != null){
            resId = intent.getStringExtra("res_id")
            resName = intent.getStringExtra("res_name")

        }
        else {
            Toast.makeText(this@RestaurantDetailActivity, "Intent is null", Toast.LENGTH_SHORT).show()
            finish()
        }


        setUpToolbar()


        if(ConnectionManager().checkConnectivity(this@RestaurantDetailActivity)){
            val queue = Volley.newRequestQueue(this@RestaurantDetailActivity)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/$resId"

            val jsonObjectRequest = object :JsonObjectRequest(Request.Method.GET,url,null, Response.Listener {

                try{
                    val success = it.getJSONObject("data").getBoolean("success")

                    if(success){
                        val data = it.getJSONObject("data").getJSONArray("data")

                        for(i in 0 until data.length()){

                            val resJsonObject = data.getJSONObject(i)

                            val foodItem = FoodItem(
                                resJsonObject.getString("id"),
                                resJsonObject.getString("name"),
                                resJsonObject.getString("cost_for_one"),
                                resJsonObject.getString("restaurant_id")
                            )

                            foodItemList.add(foodItem)



                        }

                        recyclerAdapter = RestaurantDetailsRecyclerAdapter(this@RestaurantDetailActivity,foodItemList,btnCart,resId,resName)
                        recyclerResDetail.adapter = recyclerAdapter
                        recyclerResDetail.layoutManager = layoutManager






                    }
                    else{
                        Toast.makeText(this@RestaurantDetailActivity,"Some error occurred",Toast.LENGTH_SHORT).show()
                    }
                }
                catch (e : Exception){
                    Toast.makeText(this@RestaurantDetailActivity,"Exception: $e",Toast.LENGTH_SHORT).show()
                }

            },Response.ErrorListener {
                Toast.makeText(this@RestaurantDetailActivity,"Volley error $it",Toast.LENGTH_SHORT).show()
                println(it)
            }){
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String,String>()
                    headers["content-type"] = "application/json"
                    headers["token"] = "2192406a25aca8"
                    return  headers
                }
            }

            queue.add(jsonObjectRequest)
        }
        else{
            val dialog = AlertDialog.Builder(this@RestaurantDetailActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings"){_,_->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("Cancel"){ _,_->
                ActivityCompat.finishAffinity(this@RestaurantDetailActivity)
            }
            dialog.create()
            dialog.show()
        }




    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if(id == android.R.id.home) {
            onBackPressed()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = resName
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}
