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
import kotlinx.android.synthetic.main.fragment_forgot_next.*
import org.json.JSONException
import org.json.JSONObject

class ForgotNextFragment(val Fcontext:Context,val mobileNumber:String) : Fragment() {

    lateinit var otp : EditText
    lateinit var passwordForgotInput: EditText
    lateinit var confirmpasswordForgotInput: EditText
    lateinit var submit : Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_forgot_next, container, false)
        otp =view.findViewById(R.id.otp)
        passwordForgotInput=view.findViewById(R.id.passwordForgot)
        confirmpasswordForgotInput=view.findViewById(R.id.confirmForgotPassword)
        submit=view.findViewById(R.id.submit)

        submit.setOnClickListener(View.OnClickListener {
            if(otp.text.isBlank()){
                otp.error = "OTP missing"
            }else{
                if(passwordForgotInput.text.isBlank())
                {
                    passwordForgotInput.error = "Password Missing"
                }else{
                    if(confirmpasswordForgotInput.text.isBlank()){
                        confirmpasswordForgotInput.error = "Confirm Password Missing"
                    }else{
                        if((passwordForgotInput.text.toString()==confirmpasswordForgotInput.text.toString()))
                        {
                            if (ConnectionManager().checkConnectivity(activity as Context)) {

                                try {

                                    val loginUser = JSONObject()

                                    loginUser.put("mobile_number", mobileNumber)
                                    loginUser.put("password", passwordForgotInput.text.toString())
                                    loginUser.put("otp", otp.text.toString())


                                    val queue= Volley.newRequestQueue(activity as Context)
                                    val url="http://13.235.250.119/v2/reset_password/fetch_result"

                                    val jsonObjectRequest = object : JsonObjectRequest(
                                        Request.Method.POST,
                                        url,
                                        loginUser,
                                        Response.Listener {

                                            val responseJsonObjectData = it.getJSONObject("data")
                                            val success = responseJsonObjectData.getBoolean("success")
                                            if (success) {
                                                progressBarForgoNext.visibility=View.VISIBLE
                                                submit.visibility=View.INVISIBLE
                                                val serverMessage=responseJsonObjectData.getString("successMessage")

                                                Toast.makeText(Fcontext, serverMessage, Toast.LENGTH_SHORT).show()

                                                val transaction = fragmentManager?.beginTransaction()
                                                transaction?.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
                                                transaction?.replace(R.id.loginregisterLayout, LoginFragment(Fcontext))
                                                transaction?.commit()


                                            } else {
                                                progressBarForgoNext.visibility=View.INVISIBLE
                                                submit.visibility=View.VISIBLE
                                                val responseMessageServer = responseJsonObjectData.getString("errorMessage")
                                                Toast.makeText(Fcontext, responseMessageServer.toString(), Toast.LENGTH_SHORT).show()

                                            }

                                        },
                                        Response.ErrorListener {
                                            Toast.makeText(Fcontext, "mSome Error occurred!!!", Toast.LENGTH_SHORT).show()

                                        }) {
                                        override fun getHeaders(): MutableMap<String, String> {
                                            val headers = HashMap<String, String>()

                                            headers["Content-type"] = "application/json"
                                            headers["token"]="20d7484e26c9b7"

                                            return headers
                                        }
                                    }

                                    queue.add(jsonObjectRequest)

                                } catch (e: JSONException) {
                                    Toast.makeText(Fcontext, "Some unexpected error occurred!!!", Toast.LENGTH_SHORT).show()
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
                        }else{
                            confirmpasswordForgotInput.setError("Passwords don't match")
                        }
                    }
                }
            }
        })
        return view
    }
}