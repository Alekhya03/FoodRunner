package com.alekhya.foodrunner.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.alekhya.foodrunner.R
import com.alekhya.foodrunner.activity.MainActivity
import com.alekhya.foodrunner.util.ConnectionManager
import kotlinx.android.synthetic.main.fragment_login.*
import org.json.JSONException
import org.json.JSONObject

class LoginFragment(val Lcontext: Context): Fragment() {

    lateinit  var mobilenumber: EditText
    lateinit var password : EditText
    lateinit var forgot: TextView
    lateinit var donthaveaccount: TextView
    lateinit var loginbtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val a = Intent(Intent.ACTION_MAIN)
                a.addCategory(Intent.CATEGORY_HOME)
                a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(a)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view=inflater.inflate(R.layout.fragment_login, container, false)

        mobilenumber=view.findViewById(R.id.mobileNumberLogin)
        password=view.findViewById(R.id.passwordLogin)
        forgot=view.findViewById(R.id.forgotPassword)
        donthaveaccount=view.findViewById(R.id.dontHaveAnAccount)
        loginbtn=view.findViewById(R.id.buttonLogin)

        forgot.setOnClickListener(
            View.OnClickListener {
                setPasswordForgotFragment()
            }
        )

        donthaveaccount.setOnClickListener(
            View.OnClickListener {
                setRegisterFragment()
            }
        )

        loginbtn.setOnClickListener(View.OnClickListener {


            if (mobilenumber.text.isBlank()) {
                mobilenumber.error = "Mobile Number Missing"

            }
            else{
                if(password.text.isBlank())
                {
                    password.error = "Missing Password"
                }else{

                    userLogin()
                }
            }

        })




        return view
    }

    private fun setRegisterFragment()
    {
        val transaction = fragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction?.replace(R.id.loginregisterLayout, RegisterFragment(Lcontext))
        transaction?.commit()
    }

    fun userLogin()
    {

        val sharedPreferences=Lcontext.getSharedPreferences(getString(R.string.shared_preferences),Context.MODE_PRIVATE)

        if (ConnectionManager().checkConnectivity(activity as Context)) {

            try {

                val loginUser = JSONObject()
                loginUser.put("mobile_number", mobilenumber.text)
                loginUser.put("password", password.text)

                val queue=Volley.newRequestQueue(activity as Context)
                val url="http://13.235.250.119/v2/login/fetch_result/"

                val jsonObjectRequest = object : JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    loginUser,
                    Response.Listener {

                        val responseJsonObjectData = it.getJSONObject("data")
                        val success = responseJsonObjectData.getBoolean("success")


                        if (success) {

                            progressBarLogin.visibility=View.VISIBLE
                            loginbtn.visibility=View.INVISIBLE

                            val data = responseJsonObjectData.getJSONObject("data")
                            sharedPreferences.edit().putBoolean("user_logged_in", true).apply()
                            sharedPreferences.edit().putString("user_id", data.getString("user_id")).apply()
                            sharedPreferences.edit().putString("name", data.getString("name")).apply()
                            sharedPreferences.edit().putString("email", data.getString("email")).apply()
                            sharedPreferences.edit().putString("mobile_number", data.getString("mobile_number")).apply()
                            sharedPreferences.edit().putString("address", data.getString("address")).apply()

                            Toast.makeText(Lcontext, "Welcome "+data.getString("name"), Toast.LENGTH_SHORT).show()

                            val intent=Intent(activity as Context,MainActivity::class.java)
                            startActivity(intent)
                            activity?.finish();

                        } else {

                            val responseMessageServer =
                                responseJsonObjectData.getString("errorMessage")
                            Toast.makeText(Lcontext, responseMessageServer.toString(), Toast.LENGTH_SHORT).show()

                        }

                    },
                    Response.ErrorListener {

                        Toast.makeText(Lcontext, "Some Error occurred!!!", Toast.LENGTH_SHORT).show()



                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()

                        headers["Content-type"]= "application/json"
                        headers["token"]="20d7484e26c9b7"

                        return headers
                    }
                }

                queue.add(jsonObjectRequest)

            } catch (e: JSONException){

                Toast.makeText(Lcontext, "Some unexpected error occurred!!!", Toast.LENGTH_SHORT).show()

            }

        }else
        {

            val alterDialog=androidx.appcompat.app.AlertDialog.Builder(activity as Context)

            alterDialog.setTitle("No Internet")
            alterDialog.setMessage("Internet Connection can't be establish!")
            alterDialog.setPositiveButton("Open Settings"){text,listener->
                val settingsIntent= Intent(Settings.ACTION_WIFI_SETTINGS)//open wifi settings
                startActivity(settingsIntent)

            }

            alterDialog.setNegativeButton("Exit"){ text,listener->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            alterDialog.create()
            alterDialog.show()

        }
    }

    fun setPasswordForgotFragment()
    {
        val transaction = fragmentManager?.beginTransaction()
        transaction?.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction?.replace(R.id.loginregisterLayout, ForgotFragment(Lcontext))
        transaction?.commit()
    }


}