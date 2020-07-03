package com.kirtik.foodrunner.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.kirtik.foodrunner.R
import com.kirtik.foodrunner.util.ConnectionManager
import org.json.JSONObject

class NewPasswordActivity : AppCompatActivity() {

    lateinit var mobileNumber : String
    lateinit var etOTP : EditText
    lateinit var etNewPassword : EditText
    lateinit var etConfirmPassword : EditText
    lateinit var btnNewPassword : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_password)
        supportActionBar?.hide()


        if(intent!=null){
            mobileNumber = intent.getStringExtra("mobile_number")
        }
        else{
            Toast.makeText(this@NewPasswordActivity,"Intent is null",Toast.LENGTH_SHORT).show()
            finish()
        }

        etOTP = findViewById(R.id.etOTP)
        etNewPassword = findViewById(R.id.etNewPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnNewPassword = findViewById(R.id.btnNewPassword)

        btnNewPassword.setOnClickListener {
            if (etNewPassword.text.toString() == etConfirmPassword.text.toString() && etNewPassword.text.length>=4) {

                if(ConnectionManager().checkConnectivity(this@NewPasswordActivity)){
                    val queue = Volley.newRequestQueue(this@NewPasswordActivity)
                    val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                    val jsonParams = JSONObject()

                    jsonParams.put("mobile_number", mobileNumber)
                    jsonParams.put("password",etNewPassword.text.toString())
                    jsonParams.put("otp",etOTP.text.toString())

                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST,url,jsonParams,
                        Response.Listener {

                            try{
                                val success = it.getJSONObject("data").getBoolean("success")

                                if(success){
                                    Toast.makeText(this@NewPasswordActivity,"Password has been successfully changed",Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this@NewPasswordActivity,LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    Toast.makeText(this@NewPasswordActivity,"Invalid Credentials",Toast.LENGTH_SHORT).show()
                                }

                            }
                            catch (e:Exception){
                                Toast.makeText(this@NewPasswordActivity,"Exception $e",Toast.LENGTH_SHORT).show()
                            }

                        },
                        Response.ErrorListener {
                            Toast.makeText(this@NewPasswordActivity,"$it", Toast.LENGTH_SHORT).show()
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
                    val dialog = AlertDialog.Builder(this@NewPasswordActivity)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings"){_,_->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Cancel"){ _,_->
                        ActivityCompat.finishAffinity(this@NewPasswordActivity)
                    }
                    dialog.create()
                    dialog.show()
                }


            }
            else{
                Toast.makeText(this@NewPasswordActivity,"Invalid credentials",Toast.LENGTH_SHORT).show()
            }
        }





    }
}
