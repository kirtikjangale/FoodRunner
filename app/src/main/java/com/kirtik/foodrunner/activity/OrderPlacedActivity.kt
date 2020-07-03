package com.kirtik.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.kirtik.foodrunner.R

class OrderPlacedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)

        Handler().postDelayed({
            val loginAct = Intent(this@OrderPlacedActivity,HomePageActivity::class.java)
            startActivity(loginAct)
            finish()
        },2000)
    }
}
