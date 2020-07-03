package com.kirtik.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.kirtik.foodrunner.R
import com.kirtik.foodrunner.activity.RestaurantDetailActivity
import com.kirtik.foodrunner.database.FoodEntity
import com.kirtik.foodrunner.database.FoodItemDatabase
import com.kirtik.foodrunner.database.ResItemDatabase
import com.kirtik.foodrunner.model.Restaurant
import com.like.LikeButton
import com.like.OnLikeListener
import com.squareup.picasso.Picasso


class HomeRecyclerAdapter(val context: Context, val resList : List<Restaurant>) : RecyclerView.Adapter <HomeRecyclerAdapter.HomeViewHolder>() {

    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val resName : TextView = view.findViewById(R.id.txtResName)
        val resPrice : TextView = view.findViewById(R.id.txtResPrice)
        val resRating : TextView = view.findViewById(R.id.txtResRating)
        val resImage : ImageView = view.findViewById(R.id.imgResImage)

        val llContent : LinearLayout = view.findViewById(R.id.llContent)
        val likeButton : LikeButton = view.findViewById(R.id.star_button)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_allrestaurants_single_row,parent,false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return resList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val res = resList[position]

        holder.resName.text = res.resName
        holder.resPrice.text = res.resPrice + "/person"
        holder.resRating.text = res.resRating
        Picasso.get().load(res.resImage).error(R.drawable.app_icon).into(holder.resImage)

        val favRes = com.kirtik.foodrunner.database.Restaurant(
            res.resId,
            res.resName,
            res.resRating,
            res.resPrice,
            res.resImage
        )

        if(DBAsyncTask(context,favRes,3).execute().get()){
            holder.likeButton.setLiked(true)
            println(favRes.resName)
        }
        else
            holder.likeButton.setLiked(false)



        holder.likeButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton) {

                val async = DBAsyncTask(context,favRes,1).execute().get()

                if(async)
                    Toast.makeText(context,"Added to Favourites",Toast.LENGTH_SHORT).show()
            }
            override fun unLiked(likeButton: LikeButton) {

                val async = DBAsyncTask(context,favRes,2).execute().get()

                if(async)
                    Toast.makeText(context,"Deleted from Favourites",Toast.LENGTH_SHORT).show()
            }
        })

        holder.llContent.setOnClickListener {
            val intent = Intent(context,RestaurantDetailActivity::class.java)
            intent.putExtra("res_id",res.resId)
            intent.putExtra("res_name",res.resName)
            context.startActivity(intent)

        }

    }


    class DBAsyncTask(val context: Context, val restaurant: com.kirtik.foodrunner.database.Restaurant, val mode:Int): AsyncTask<Void, Void, Boolean>(){

        val db = Room.databaseBuilder(context, ResItemDatabase::class.java,"res-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when(mode){
                1->{
                    db.resDao().insertFavRestaurant(restaurant)
                    db.close()
                    return true

                }
                2->{
                    db.resDao().deleteFavRestaurant(restaurant)
                    db.close()
                    return true
                }
                3->{
                    val res:com.kirtik.foodrunner.database.Restaurant? = db.resDao().getResById(restaurant.res_id.toString())
                    db.close()
                    return res!=null
                }


            }
            return false
        }


    }

}