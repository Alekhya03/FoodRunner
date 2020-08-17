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
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.alekhya.foodrunner.R
import com.alekhya.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.fragment_forgot.*
import org.json.JSONException
import org.json.JSONObject

class ForgotFragment(val Pcontext: Context): Fragment() {

    lateinit var mobileNUmber:EditText
    lateinit var  emailAddress:EditText
    lateinit var next: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_forgot, container, false)

        mobileNUmber=view.findViewById(R.id.mobileNumberForgot)
        emailAddress=view.findViewById(R.id.emailForgot)
        next=view.findViewById(R.id.next)


        next.setOnClickListener(View.OnClickListener{
            if (mobileNUmber.text.isBlank())
            {
                mobileNUmber.setError("Mobile Number Missing")
            }
            else{
                if(emailAddress.text.isBlank()){
                    emailAddress.setError("Email Missing")
                }else{

                    if (ConnectionManager().checkConnectivity(activity as Context)) {

                        try {

                            val loginUser = JSONObject()

                            loginUser.put("mobile_number", mobileNUmber.text)
                            loginUser.put("email", emailAddress.text)

                            println(loginUser.getString("mobile_number"))
                            println(loginUser.getString("email"))


                            val queue = Volley.newRequestQueue(activity as Context)
                            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                            val jsonObjectRequest = object : JsonObjectRequest(
                                Request.Method.POST,
                                url,
                                loginUser,
                                Response.Listener {

                                    val responseJsonObjectData = it.getJSONObject("data")
                                    val success = responseJsonObjectData.getBoolean("success")
                                    if (success) {
                                        progressBarForgot.visibility=View.VISIBLE
                                        next.visibility=View.INVISIBLE
                                        val first_try=responseJsonObjectData.getBoolean("first_try")

                                        if(first_try==true){


                                            Toast.makeText(Pcontext, "OTP sent", Toast.LENGTH_SHORT).show()
                                            setPasswordFragment()
                                        }else{

                                            Toast.makeText(Pcontext, "OTP sent already", Toast.LENGTH_SHORT).show()

                                            setPasswordFragment()
                                        }

                                    } else {
                                        progressBarForgot.visibility=View.INVISIBLE
                                        next.visibility=View.VISIBLE
                                        val responseMessageServer =
                                            responseJsonObjectData.getString("errorMessage")
                                        Toast.makeText(Pcontext, responseMessageServer.toString(), Toast.LENGTH_SHORT).show()

                                    }

                                },
                                Response.ErrorListener {

                                    Toast.makeText(Pcontext, "Some Error occurred!!!", Toast.LENGTH_SHORT).show()

                                }) {
                                override fun getHeaders(): MutableMap<String, String> {
                                    val headers = HashMap<String, String>()
                                    headers["Content-type"] = "application/json"
                                    headers["token"] ="20d7484e26c9b7"
                                    return headers
                                }
                            }

                            queue.add(jsonObjectRequest)

                        } catch (e: JSONException) {
                            Toast.makeText(Pcontext, "Some unexpected error occured!", Toast.LENGTH_SHORT).show()
                        }
                    }else
                    {
                        val alterDialog=androidx.appcompat.app.AlertDialog.Builder(activity as Context)

                        alterDialog.setTitle("No Internet")
                        alterDialog.setMessage("Internet Connection can't be establish!")
                        alterDialog.setPositiveButton("Open Settings"){text,listener->
                            val settingsIntent= Intent(Settings.ACTION_SETTINGS)//open wifi settings
                            startActivity(settingsIntent)

                        }

                        alterDialog.setNegativeButton("Exit"){ text,listener->
                            ActivityCompat.finishAffinity(activity as Activity)//closes all the instances of the app and the app closes completely
                        }
                        alterDialog.create()
                        alterDialog.show()
                    }
                    }
    }
        })
            return view
        }

    fun setPasswordFragment()
    {
        val transaction = fragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction?.replace(R.id.loginregisterLayout, ForgotNextFragment(Pcontext,mobileNUmber.text.toString()))
        transaction?.commit()
    }
}