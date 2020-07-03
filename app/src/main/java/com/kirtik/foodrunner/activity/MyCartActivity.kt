package com.kirtik.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kirtik.foodrunner.R
import com.kirtik.foodrunner.adapter.CartRecyclerAdapter
import com.kirtik.foodrunner.database.FoodEntity
import com.kirtik.foodrunner.database.FoodItemDatabase
import com.kirtik.foodrunner.util.ConnectionManager
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class MyCartActivity : AppCompatActivity() {

    lateinit var txtResName : TextView
    lateinit var sharedPreferences: SharedPreferences
    lateinit var btnPlaceOrder : Button
    var resName : String? = null
    var resId : String? = null

    lateinit var recyclerCart : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter :CartRecyclerAdapter
    lateinit var toolbar : Toolbar

    var totalPrice:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_cart)

        toolbar = findViewById(R.id.toolbar)
        setUpToolbar()
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        recyclerCart = findViewById(R.id.recyclerCart)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        layoutManager = LinearLayoutManager(this@MyCartActivity)

        var dbFoodList = RetrieveOrderedFood(this@MyCartActivity).execute().get()

        println(dbFoodList)

        if(intent != null){

            resId = intent.getStringExtra("res_id")
            resName = intent.getStringExtra("res_name")

        }
        else{
            Toast.makeText(this@MyCartActivity,"intent is null",Toast.LENGTH_LONG).show()
            finish()
        }

        txtResName = findViewById(R.id.txtResName)
        txtResName.text = "Ordering from: "+resName.toString()


        recyclerAdapter=CartRecyclerAdapter(this@MyCartActivity,dbFoodList)
        recyclerCart.adapter = recyclerAdapter
        recyclerCart.layoutManager = layoutManager


        for(item in dbFoodList){
            totalPrice+=item.foodPrice.toInt()
        }

        btnPlaceOrder.text = "Place Order(Rs $totalPrice)"

        btnPlaceOrder.setOnClickListener {

            if(ConnectionManager().checkConnectivity(this@MyCartActivity)){
                val queue = Volley.newRequestQueue(this@MyCartActivity)
                val url="http://13.235.250.119/v2/place_order/fetch_result/"

                val jsonParams = JSONObject()

                jsonParams.put("user_id",sharedPreferences.getString("user_id","-1"))
                jsonParams.put("restaurant_id",resId.toString())
                jsonParams.put("total_cost",totalPrice.toString())

                val food = JSONArray()

                for(item in dbFoodList){
                    val foodObject = JSONObject()
                    foodObject.put("food_item_id",item.food_id.toString())
                    food.put(foodObject)

                }
                jsonParams.put("food",food)
                println(jsonParams)
                val jsonRequest = object: JsonObjectRequest(Request.Method.POST,url,jsonParams,
                    Response.Listener{
                        println(it)
                        try {
                            val success = it.getJSONObject("data").getBoolean("success")

                            if(success){
                                val intent = Intent(this@MyCartActivity,OrderPlacedActivity::class.java)
                                startActivity(intent)
                                finish()


                            }
                            else{
                                Toast.makeText(this@MyCartActivity,"Request response is false",Toast.LENGTH_SHORT).show()
                            }
                        }
                        catch (e : Exception){
                            println(e)
                            Toast.makeText(this@MyCartActivity,"Exception",Toast.LENGTH_SHORT).show()
                        }

                    }, Response.ErrorListener {
                        Toast.makeText(this@MyCartActivity,"$it",Toast.LENGTH_SHORT).show()
                    }){

                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String,String>()
                        headers["content-type"] = "application/json"
                        headers["token"] = "2192406a25aca8"
                        return  headers
                    }
                }


                queue.add(jsonRequest)
            }
            else{
                val dialog = AlertDialog.Builder(this@MyCartActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings"){_,_->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Cancel"){ _,_->
                    ActivityCompat.finishAffinity(this@MyCartActivity)
                }
                dialog.create()
                dialog.show()
            }



        }

    }
    class RetrieveOrderedFood(val context: Context) : AsyncTask<Void, Void, List<FoodEntity>>(){
        override fun doInBackground(vararg params: Void?): List<FoodEntity> {

            val db = Room.databaseBuilder(context,FoodItemDatabase::class.java,"food-db").build()

            return db.foodDao().getAllFoodItems()


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
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true) //enable home button
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //display home button

    }
}
