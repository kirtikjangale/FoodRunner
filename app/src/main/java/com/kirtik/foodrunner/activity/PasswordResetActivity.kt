package com.kirtik.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.kirtik.foodrunner.R
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class PasswordResetActivity : AppCompatActivity() {

    lateinit var etMobileNumber : EditText
    lateinit var etEmail : EditText
    lateinit var btnNext : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            val queue = Volley.newRequestQueue(this@PasswordResetActivity)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

            val params = JSONObject()

            params.put("mobile_number",etMobileNumber.text.toString())
            params.put("email",etEmail.text.toString())

            val jsonRequest = object : JsonObjectRequest(Request.Method.POST,url,params,Response.Listener {

                try{
                    val success = it.getJSONObject("data").getBoolean("success")

                    if(success){
                        val intent = Intent(this@PasswordResetActivity,NewPasswordActivity::class.java)
                        intent.putExtra("mobile_number",etMobileNumber.text.toString())
                        startActivity(intent)
                    }
                    else{
                        Toast.makeText(this@PasswordResetActivity,"Invalid Credentials",Toast.LENGTH_SHORT).show()
                    }

                }
                catch (e:Exception){
                    Toast.makeText(this@PasswordResetActivity,"Exception $e",Toast.LENGTH_SHORT).show()
                }

            },Response.ErrorListener {
                Toast.makeText(this@PasswordResetActivity,"$it", Toast.LENGTH_SHORT).show()
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


    }
}
