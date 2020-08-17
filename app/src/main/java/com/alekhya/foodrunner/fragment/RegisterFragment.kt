package com.alekhya.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.alekhya.foodrunner.R
import com.alekhya.foodrunner.activity.MainActivity
import com.alekhya.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.fragment_register.*
import org.json.JSONException
import org.json.JSONObject


class RegisterFragment(val Rcontext : Context) : Fragment() {

    lateinit var name : EditText
    lateinit var emailaddress : EditText
    lateinit var mobilenumber : EditText
    lateinit var delieveryaddress : EditText
    lateinit var password : EditText
    lateinit var confirmpassword : EditText
    lateinit var registerButton : Button
    lateinit var toolbarRegister: Toolbar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_register, container, false)
        name =view.findViewById(R.id.nameRegister)
        emailaddress=view.findViewById(R.id.emailRegister)
        mobilenumber=view.findViewById(R.id.mobileNumberRegister)
        delieveryaddress=view.findViewById(R.id.addressRegister)
        password=view.findViewById(R.id.passwordRegsiter)
        confirmpassword=view.findViewById(R.id.confirmpasswordRegsiter)
        registerButton=view.findViewById(R.id.buttonRegister)
        toolbarRegister=view.findViewById(R.id.toolbarRegister)

            setUpToolbar()
            registerButton.setOnClickListener(View.OnClickListener { userRegistration() })
        return view
    }

    private fun setUpToolbar()
    {
        (activity as AppCompatActivity).setSupportActionBar(toolbarRegister)
        (activity as AppCompatActivity).supportActionBar?.title="Register Yourself"
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    private fun userRegistration()
    {
        val sharedPreferences=Rcontext.getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE)


        sharedPreferences.edit().putBoolean("user_logged_in", false).apply()

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            if (checkInputs()){
                try {
                    val registerUser = JSONObject()
                    registerUser.put("name", name.text)
                    registerUser.put("mobile_number", mobilenumber.text)
                    registerUser.put("password", password.text)
                    registerUser.put("address", delieveryaddress.text)
                    registerUser.put("email", emailaddress.text)


                    val queue = Volley.newRequestQueue(activity as Context)
                    val url="http://13.235.250.119/v2/register/fetch_result"

                    val jsonObjectRequest = object : JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        registerUser,
                        Response.Listener {

                            val responseJsonObjectData = it.getJSONObject("data")
                            val success = responseJsonObjectData.getBoolean("success")
                            if (success) {
                                registerButton.visibility=View.INVISIBLE
                                progressBarRegister.visibility=View.VISIBLE

                                val data = responseJsonObjectData.getJSONObject("data")
                                sharedPreferences.edit().putBoolean("user_logged_in", true).apply()
                                sharedPreferences.edit().putString("user_id", data.getString("user_id")).apply()
                                sharedPreferences.edit().putString("name", data.getString("name")).apply()
                                sharedPreferences.edit().putString("email", data.getString("email")).apply()
                                sharedPreferences.edit().putString("mobile_number", data.getString("mobile_number")).apply()
                                sharedPreferences.edit().putString("address", data.getString("address")).apply()


                                Toast.makeText(Rcontext, "Registered successfully", Toast.LENGTH_SHORT).show()

                                val intent=Intent(activity as Context,MainActivity::class.java)
                                startActivity(intent)
                                activity?.finish();


                            } else {

                                val responseMessageServer = responseJsonObjectData.getString("errorMessage")
                                Toast.makeText(Rcontext, responseMessageServer.toString(), Toast.LENGTH_SHORT).show()
                            }

                        },
                        Response.ErrorListener {
                            Toast.makeText(Rcontext, "Some Error occurred!!!", Toast.LENGTH_SHORT).show()

                        }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"]= "application/json"
                            headers["token"]= "20d7484e26c9b7"
                            return headers
                        }
                    }

                    queue.add(jsonObjectRequest)

                } catch (e: JSONException) {
                    Toast.makeText(Rcontext, "Some unexpected error occurred!!!", Toast.LENGTH_SHORT).show()

                }
            }
        }else
        {
            val dialog=androidx.appcompat.app.AlertDialog.Builder(activity as Context)

            dialog.setTitle("No Internet")
            dialog.setMessage("Internet Connection can't be establish!")
            dialog.setPositiveButton("Open Settings"){ text, listener->
                val settingsIntent= Intent(Settings.ACTION_WIFI_SETTINGS)
                startActivity(settingsIntent)

            }

            dialog.setNegativeButton("Exit"){ text, listener->
                ActivityCompat.finishAffinity(activity as Activity)//closes all the instances of the app and the app closes completely
            }
            dialog.create()
            dialog.show()
        }
    }


    fun checkInputs():Boolean {
        var count = 0
        if (name.text.isBlank()) {
            name.setError("Name Missing!")
        }
        else {
            count++
        }

        if (mobilenumber.text.isBlank()) {
            mobilenumber.setError("Number Missing!")
        }
        else {
            count++
        }

        if (emailaddress.text.isBlank()) {
            emailaddress.setError("E-mail Address Missing!")
        }
        else {
            count++
        }

        if (delieveryaddress.text.isBlank()) {
            delieveryaddress.setError("Address Missing!")
        }
        else {
            count++
        }

        if (confirmpassword.text.isBlank()) {
            confirmpassword.setError(" Missing!")
        }
        else {
            count++
        }

        if (password.text.isBlank()) {
            password.setError("Password Missing!")
        } else
        {
            count++
        }

        if (password.text.isNotBlank() && confirmpassword.text.isNotBlank())
        {
            if (password.text.toString() == confirmpassword.text.toString()) {
            count++
        } else
        {
            password.setError("Password don't match")
        }
        }

        return count==7
    }
}