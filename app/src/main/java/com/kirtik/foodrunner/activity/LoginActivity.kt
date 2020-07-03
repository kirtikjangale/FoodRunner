package com.kirtik.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kirtik.foodrunner.R
import com.kirtik.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class LoginActivity : AppCompatActivity() {

    lateinit var etMobileNumber : EditText
    lateinit var etPassword : EditText
    lateinit var btnLogin : Button
    lateinit var txtForgotPassword : TextView
    lateinit var txtRegister : TextView

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)

        if(isLoggedIn){
            val intent = Intent(this@LoginActivity,HomePageActivity::class.java)
            startActivity(intent)
            finish()
        }

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegister = findViewById(R.id.txtRegister)

        txtRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity,RegisterActivity::class.java)
            startActivity(intent)
        }

        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity,PasswordResetActivity::class.java)
            startActivity(intent    )
        }

        btnLogin.setOnClickListener {
            if(checkMobile(etMobileNumber.text.toString()) && checkPassword(etPassword.text.toString())){


                if(ConnectionManager().checkConnectivity(this@LoginActivity)){
                    val queue = Volley.newRequestQueue(this@LoginActivity)
                    val url = "http://13.235.250.119/v2/login/fetch_result/"

                    val jsonParams = JSONObject()

                    jsonParams.put("mobile_number",etMobileNumber.text.toString())
                    jsonParams.put("password",etPassword.text.toString())

                    val jsonRequest = object : JsonObjectRequest(Request.Method.POST,url,jsonParams,
                        Response.Listener{
                            println(it)
                            try {
                                val success = it.getJSONObject("data").getBoolean("success")

                                if(success){

                                    val userId = it.getJSONObject("data").getJSONObject("data").getString("user_id")
                                    val name = it.getJSONObject("data").getJSONObject("data").getString("name")
                                    val email = it.getJSONObject("data").getJSONObject("data").getString("email")
                                    val mobileNumber = it.getJSONObject("data").getJSONObject("data").getString("mobile_number")
                                    val address = it.getJSONObject("data").getJSONObject("data").getString("address")

                                    savePreferences(userId,name,email,mobileNumber,address)

                                    val intent = Intent(this@LoginActivity,HomePageActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    Toast.makeText(this@LoginActivity,"Invalid Credentials",Toast.LENGTH_SHORT).show()
                                }
                            }
                            catch (e : Exception){
                                println(e)
                                Toast.makeText(this@LoginActivity,"Exception",Toast.LENGTH_SHORT).show()
                            }

                        }, Response.ErrorListener {
                            Toast.makeText(this@LoginActivity,"$it",Toast.LENGTH_SHORT).show()
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
                    val dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings"){_,_->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Cancel"){ _,_->
                        ActivityCompat.finishAffinity(this@LoginActivity)
                    }
                    dialog.create()
                    dialog.show()
                }


            }
        }

    }

    fun savePreferences(userId:String,name: String,email: String,number: String,address: String){
        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
        sharedPreferences.edit().putString("name",name).apply()
        sharedPreferences.edit().putString("mobile_number",number).apply()
        sharedPreferences.edit().putString("address",address).apply()
        sharedPreferences.edit().putString("email",email).apply()
        sharedPreferences.edit().putString("user_id",userId).apply()
    }

    private fun checkMobile(number:String) : Boolean{
        val ans = number.length == 10
        if(!ans){
            Toast.makeText(this@LoginActivity,"Invalid Mobile Number", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkPassword(password:String) : Boolean{
        val ans = password.length >= 4
        if(!ans){
            Toast.makeText(this@LoginActivity,"Invalid Password",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}
