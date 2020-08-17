package com.alekhya.foodrunner.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.alekhya.foodrunner.R

class ProfileFragment(private val profilecontext: Context) : Fragment() {

    lateinit var name: TextView
    lateinit var email:TextView
    lateinit var mobileNumber:TextView
    lateinit var address:TextView

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_profile, container, false)

        name=view.findViewById(R.id.profileName)
        email=view.findViewById(R.id.profileEmail)
        mobileNumber=view.findViewById(R.id.profileMobileNumber)
        address=view.findViewById(R.id.profileDelievery)

        val sharedPreferences=profilecontext.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        name.text=sharedPreferences.getString("name","")
        email.text=sharedPreferences.getString("email","")
        mobileNumber.text="+91-"+sharedPreferences.getString("mobile_number","")
        address.text=sharedPreferences.getString("address","")

        return view
    }

}