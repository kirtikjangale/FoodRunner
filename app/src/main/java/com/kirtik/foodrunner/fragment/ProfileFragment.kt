package com.kirtik.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView

import com.kirtik.foodrunner.R

/**
 * A simple [Fragment] subclass.
 */
class ProfileFragment : Fragment() {

    lateinit var etName : TextView
    lateinit var etMobileNumber : TextView
    lateinit var etEmail : TextView
    lateinit var etAddress : TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences = activity!!.getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        etName = view.findViewById(R.id.etName)
        etAddress = view.findViewById(R.id.etDeliveryAddress)
        etEmail = view.findViewById(R.id.etEmail)
        etMobileNumber = view.findViewById(R.id.etMobileNumber)


        etName.text = sharedPreferences.getString("name","name")
        etAddress.text = sharedPreferences.getString("address","address")
        etEmail.text = sharedPreferences.getString("email","email")
        etMobileNumber.text = sharedPreferences.getString("mobile_number","mobile_number")

        return view
    }

}
