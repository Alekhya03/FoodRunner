package com.alekhya.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.alekhya.foodrunner.R
import com.alekhya.foodrunner.fragment.*
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var sharedPreferencess: SharedPreferences
    lateinit var currUserName : TextView
    lateinit var currUserMobileNumber : TextView

    var previousMenuItem: MenuItem?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferencess=getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE)
        drawerLayout=findViewById(R.id.drawerLayout)
        coordinatorLayout=findViewById(R.id.coordinatorLayout)
        toolbar=findViewById(R.id.toolbar)
        frameLayout=findViewById(R.id.frame)
        navigationView=findViewById(R.id.navigationView)
        val headerView=navigationView.getHeaderView(0)
        currUserName=headerView.findViewById(R.id.drawerName)
        currUserMobileNumber=headerView.findViewById(R.id.drawerNumber)

        currUserName.text=sharedPreferencess.getString("name","alekhya")
        currUserMobileNumber.text="+91-"+sharedPreferencess.getString("mobile_number","9876543210")
        setUpToolbar()
        openHome()

        val actionBarDrawerToggle=
            ActionBarDrawerToggle(this@MainActivity,drawerLayout, R.string.open_drawer, R.string.close_drawer)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if(previousMenuItem!=null)
            {
                previousMenuItem?.isChecked=true
            }

            it.isCheckable=true
            it.isChecked=true
            previousMenuItem=it

            when(it.itemId)
            {
                R.id.menu_home ->{
                    openHome()

                    drawerLayout.closeDrawers()
                }
                R.id.menu_profile ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ProfileFragment(this)
                        )
                        .commit()

                    supportActionBar?.title="Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.menu_orders ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            OrderFragment()
                        )
                        .commit()

                    supportActionBar?.title="Orders"
                    drawerLayout.closeDrawers()
                }
                R.id.menu_favorties ->{

                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavoritesFragment()
                        )
                        .commit()

                    supportActionBar?.title="Favourites"
                    drawerLayout.closeDrawers()
                }

                R.id.menu_faq ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FaqFragment()
                        )
                        .commit()

                    supportActionBar?.title="FAQs"
                    drawerLayout.closeDrawers()
                }
                R.id.menu_aboutapp ->{
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            AboutFragment()
                        )
                        .commit()

                    supportActionBar?.title="About App"
                    drawerLayout.closeDrawers()
                }

                R.id.menu_logout ->{
                    drawerLayout.closeDrawers()
                    val alterDialog=androidx.appcompat.app.AlertDialog.Builder(this)
                    alterDialog.setTitle("Confirmation")
                    alterDialog.setMessage("Are you sure you want to exit?")
                    alterDialog.setPositiveButton("Yes"){text,listener->
                        sharedPreferencess.edit().putBoolean("user_logged_in",false).apply()
                        ActivityCompat.finishAffinity(this)
                        val intent= Intent(this,LoginRegisterActivity::class.java)
                        startActivity(intent)
                        finish();
                    }
                    alterDialog.setNegativeButton("No"){ text,listener->
                    }
                    alterDialog.create()
                    alterDialog.show()

                }

            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolbar()
    {
        setSupportActionBar(toolbar)
        supportActionBar?.title="Toolbar"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id=item.itemId

        if(id == android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openHome()
    {
        val fragment= HomeFragment(this)
        val transaction= supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame,fragment)
        transaction.commit()

        supportActionBar?.title="All Restaurants"
        navigationView.setCheckedItem(R.id.menu_home)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when(frag)
        {
            !is HomeFragment -> openHome()

            else ->  super.onBackPressed()
        }
    }
}