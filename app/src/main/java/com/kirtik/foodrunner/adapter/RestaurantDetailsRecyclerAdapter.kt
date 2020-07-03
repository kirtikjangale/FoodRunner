package com.kirtik.foodrunner.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.kirtik.foodrunner.R
import com.kirtik.foodrunner.activity.MyCartActivity
import com.kirtik.foodrunner.database.FoodDao
import com.kirtik.foodrunner.database.FoodEntity
import com.kirtik.foodrunner.database.FoodItemDatabase
import com.kirtik.foodrunner.model.FoodItem

class RestaurantDetailsRecyclerAdapter(val context: Context,val itemList : List<FoodItem>,val btnCart:Button,val resId : String?,val resName:String?) : RecyclerView.Adapter <RestaurantDetailsRecyclerAdapter.DetailViewHolder>() {

    var count = 1
    var itemcount=0;



    class DetailViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val txtSerialNumber : TextView = view.findViewById(R.id.txtSerialNumber)
        val txtFoodItemName : TextView = view.findViewById(R.id.txtFoodItemName)
        val txtFoodItemPrice : TextView = view.findViewById(R.id.txtFoodItemPrice)
        val btnAddItem : ToggleButton = view.findViewById(R.id.btnAddItem)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_resdetail_single_row,parent,false)

        DeleteItems(context).execute().get()
    

        return DetailViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }



    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        val item = itemList[position]
        holder.txtSerialNumber.text = count.toString()
        holder.txtFoodItemName.text = item.itemName
        holder.txtFoodItemPrice.text = item.itemCost
        count++

        holder.btnAddItem.setOnClickListener{
            if(holder.btnAddItem.isChecked){
                holder.btnAddItem.setBackgroundColor(ContextCompat.getColor(context,R.color.yellow))
                itemcount++


                val foodEntity = FoodEntity(item.itemId,item.itemName,item.itemCost)
                DBAsyncTask(context,foodEntity,1).execute().get()

            }
            else{
                holder.btnAddItem.setBackgroundColor(ContextCompat.getColor(context,R.color.colorPrimary))
                itemcount--

                val foodEntity = FoodEntity(item.itemId,item.itemName,item.itemCost)
                DBAsyncTask(context,foodEntity,2).execute().get()


            }

            if(itemcount>0)
                btnCart.visibility=View.VISIBLE
            else
                btnCart.visibility=View.GONE


        }


        btnCart.setOnClickListener {
            val intent = Intent(context.applicationContext,MyCartActivity::class.java)
            intent.putExtra("res_name",resName)
            intent.putExtra("res_id",resId)
            context.startActivity(intent)


        }



    }


    class DBAsyncTask(val context: Context,val foodEntity: FoodEntity,val mode:Int): AsyncTask<Void, Void, Boolean>(){

        val db = Room.databaseBuilder(context,FoodItemDatabase::class.java,"food-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1->{
                   db.foodDao().insertFoodItem(foodEntity)
                    db.close()
                    return true

                }
                2->{
                    db.foodDao().deleteFoodItem(foodEntity)
                    db.close()
                    return true
                }


            }
            return false
        }


    }

    class DeleteItems(val context: Context) : AsyncTask<Void,Void,Boolean>(){
        override fun doInBackground(vararg params: Void?):Boolean{

            val db = Room.databaseBuilder(context,FoodItemDatabase::class.java,"food-db").build()

            db.foodDao().deleteAllFoodItems()
            db.close()
            return true

        }

    }



}