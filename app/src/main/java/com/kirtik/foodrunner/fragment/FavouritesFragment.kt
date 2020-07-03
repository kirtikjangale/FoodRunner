package com.kirtik.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room

import com.kirtik.foodrunner.R
import com.kirtik.foodrunner.adapter.FavouritesRecyclerAdapter
import com.kirtik.foodrunner.adapter.HomeRecyclerAdapter
import com.kirtik.foodrunner.database.ResItemDatabase
import com.kirtik.foodrunner.database.Restaurant


class FavouritesFragment : Fragment() {

    lateinit var recyclerHome : RecyclerView
    lateinit var layoutManager : RecyclerView.LayoutManager

    lateinit var recyclerAdapter: FavouritesRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)

        val favResList = RetrieveFavRestaurants(activity as Context).execute().get()

        recyclerHome = view.findViewById(R.id.recyclerFavourites)
        layoutManager = LinearLayoutManager(activity)

        recyclerAdapter = FavouritesRecyclerAdapter(activity as Context,favResList)
        recyclerHome.adapter = recyclerAdapter
        recyclerHome.layoutManager = layoutManager


        return view
    }

    class RetrieveFavRestaurants(val context: Context) : AsyncTask<Void, Void, List<Restaurant>>(){
        override fun doInBackground(vararg params: Void?): List<Restaurant> {

            val db = Room.databaseBuilder(context, ResItemDatabase::class.java,"res-db").build()

            return db.resDao().getAllFavRestaurants()


        }

    }

}
