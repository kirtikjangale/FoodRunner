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
import android.widget.Toast
import com.kirtik.foodrunner.R
import android.util.Patterns
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kirtik.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception

class RegisterActivity : AppCompatActivity() {

    lateinit var etName : EditText
    lateinit var etEmail : EditText
    lateinit var etMobileNumber : EditText
    lateinit var etDeliveryAddress : EditText
    lateinit var etPassword : EditText
    lateinit var etConfirmPassword : EditText
    lateinit var btnRegister : Button

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        supportActionBar?.title = "Register Yourself"

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            if(checkName(etName.text.toString()) && checkEmail(etEmail.text.toString()) && checkMobile(etMobileNumber.text.toString()) && checkAddress(etDeliveryAddress.text.toString()) && checkPassword(etPassword.text.toString()) && confirmPassword(etPassword.text.toString(),etConfirmPassword.text.toString())){

                if(ConnectionManager().checkConnectivity(this@RegisterActivity)){
                    val queue = Volley.newRequestQueue(this@RegisterActivity)
                    val url = "http://13.235.250.119/v2/register/fetch_result"

                    val jsonParams = JSONObject()

                    jsonParams.put("name",etName.text.toString())
                    jsonParams.put("mobile_number",etMobileNumber.text.toString())
                    jsonParams.put("password",etPassword.text.toString())
                    jsonParams.put("address",etDeliveryAddress.text.toString())
                    jsonParams.put("email",etEmail.text.toString())

                    println(jsonParams)

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

                                    val intent = Intent(this@RegisterActivity,HomePageActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    Toast.makeText(this@RegisterActivity,"Some error occurred",Toast.LENGTH_SHORT).show()
                                }
                            }
                            catch (e : Exception){
                                println(e)
                                Toast.makeText(this@RegisterActivity,"Exception",Toast.LENGTH_SHORT).show()
                            }

                        },Response.ErrorListener {
                            Toast.makeText(this@RegisterActivity,"$it",Toast.LENGTH_SHORT).show()
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
                else {
                    val dialog = AlertDialog.Builder(this@RegisterActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings"){_,_->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Cancel"){ _,_->
                        ActivityCompat.finishAffinity(this@RegisterActivity)
                    }
                    dialog.create()
                    dialog.show()
                }

            }

        }
    }

    private fun checkName(name:String) : Boolean{
        if(name.length < 3){
            Toast.makeText(this@RegisterActivity,"Invalid Name",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkEmail(email:String) :Boolean{
        val ans = Patterns.EMAIL_ADDRESS.toRegex().matches(email)
        if (!ans){
            Toast.makeText(this@RegisterActivity, "Invalid Email", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkMobile(number:String) : Boolean{
       val ans = number.length == 10
        if(!ans){
            Toast.makeText(this@RegisterActivity,"Invalid Mobile Number",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkAddress(address:String) : Boolean{
        val ans = address.isNotEmpty()
        if(!ans){
            Toast.makeText(this@RegisterActivity,"Invalid Address",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun checkPassword(password:String) : Boolean{
        val ans = password.length >= 4
        if(!ans){
            Toast.makeText(this@RegisterActivity,"Invalid Password",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun confirmPassword(password: String, confirm_password:String) : Boolean{
        val ans = password == confirm_password
        if(!ans){
            Toast.makeText(this@RegisterActivity,"Password and Confirm Password does not match",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun savePreferences(userId:String,name: String,email: String,number: String,address: String){
        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
        sharedPreferences.edit().putString("name",name).apply()
        sharedPreferences.edit().putString("mobile_number",number).apply()
        sharedPreferences.edit().putString("address",address).apply()
        sharedPreferences.edit().putString("email",email).apply()
        sharedPreferences.edit().putString("user_id",userId).apply()
}

}
