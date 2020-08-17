package com.alekhya.foodrunner.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.alekhya.foodrunner.R

class OrderSuccessActivity : AppCompatActivity() {
    lateinit var okay: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        okay=findViewById(R.id.btnOk)

        okay.setOnClickListener(View.OnClickListener {
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        })
    }

    override fun onBackPressed() {

    }
}