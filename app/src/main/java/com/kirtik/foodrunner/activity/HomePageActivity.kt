package com.kirtik.foodrunner.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.kirtik.foodrunner.R
import com.kirtik.foodrunner.fragment.*


class HomePageActivity : AppCompatActivity() {

    lateinit var drawerLayout : DrawerLayout
    lateinit var frameLayout : FrameLayout
    lateinit var coordinatorLayout : CoordinatorLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var navigationView : NavigationView
    lateinit var sharedPreferences: SharedPreferences

    var previousMenuItem : MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        frameLayout = findViewById(R.id.frame)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigationView)
        drawerLayout = findViewById(R.id.drawerLayout)
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)


        val navigationView = findViewById<NavigationView>(R.id.navigationView)
        val headerView = navigationView.getHeaderView(0)
        val navUsername = headerView.findViewById<View>(R.id.txtName) as TextView
        val navMobileNumber = headerView.findViewById<View>(R.id.txtNumber) as TextView


        navUsername.text = sharedPreferences.getString("name","Name of the user")
        navMobileNumber.text = sharedPreferences.getString("mobile_number","Mobile Number")


        setUpToolbar()

        val actionBarDrawerToggle = ActionBarDrawerToggle(this@HomePageActivity,drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)

        actionBarDrawerToggle.syncState()

        openHome()

        navigationView.setNavigationItemSelectedListener {

            if(previousMenuItem != null){
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId){
                R.id.home ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        AllRestaurantsFragment()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "All Restaurants"
                }
                R.id.profile ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        ProfileFragment()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "My Profile"
                }
                R.id.favourites ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        FavouritesFragment()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Favourite Restaurants"
                }

                R.id.history ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        OrderHistoryFragment()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Order History"
                }

                R.id.faq ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        FaqFragment()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "FAQs"
                }
                R.id.logout ->{
                    supportFragmentManager.beginTransaction().replace(
                        R.id.frame,
                        LogoutFragment()
                    ).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Logout"
                }
            }

            return@setNavigationItemSelectedListener true
        }

    }

    private fun openHome(){
        supportFragmentManager.beginTransaction().replace(
            R.id.frame,
            AllRestaurantsFragment()
        ).addToBackStack("Home").commit()
        drawerLayout.closeDrawers()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    private fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Food Runner"
        supportActionBar?.setHomeButtonEnabled(true) //enable home button
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //display home button

    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if(id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onBackPressed() {

        when(supportFragmentManager.findFragmentById(R.id.frame)){
            !is AllRestaurantsFragment ->openHome()
            else->super.onBackPressed()
        }

    }
}
