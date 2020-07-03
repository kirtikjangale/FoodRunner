package com.kirtik.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.room.Room

import com.kirtik.foodrunner.R
import com.kirtik.foodrunner.activity.HomePageActivity
import com.kirtik.foodrunner.activity.LoginActivity
import com.kirtik.foodrunner.database.FoodItemDatabase
import com.kirtik.foodrunner.database.ResItemDatabase
import com.kirtik.foodrunner.database.Restaurant

/**
 * A simple [Fragment] subclass.
 */
class LogoutFragment : Fragment() {

    lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_logout, container, false)







        sharedPreferences = activity!!.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val dialog = AlertDialog.Builder(activity as Context)
        dialog.setTitle("Confirmation")
        dialog.setMessage("Are you sure want to exit?")
        dialog.setPositiveButton("Yes"){ _,_->

            deleteFavRestaurants(activity as Context).execute().get()
            deleteFoodItems(activity as Context).execute().get()
            sharedPreferences.edit().clear().commit()

            val intent = Intent(activity as Context,LoginActivity::class.java)
            startActivity(intent)
            activity?.finish()

        }
        dialog.setNegativeButton("No"){ _,_->
            val intent = Intent(activity as Context,HomePageActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
        dialog.create()
        dialog.show()

        return view
    }

    class deleteFavRestaurants(val context: Context) : AsyncTask<Void, Void,Boolean>(){
        override fun doInBackground(vararg params: Void?): Boolean{

            val db = Room.databaseBuilder(context, ResItemDatabase::class.java,"res-db").build()

            db.resDao().deleteAll()
            db.close()
            return true
        }

    }

    class deleteFoodItems(val context: Context) : AsyncTask<Void, Void,Boolean>(){
        override fun doInBackground(vararg params: Void?): Boolean{

            val db = Room.databaseBuilder(context,FoodItemDatabase::class.java,"food-db").build()

            db.foodDao().deleteAllFoodItems()
            db.close()
            return true
        }

    }


}
