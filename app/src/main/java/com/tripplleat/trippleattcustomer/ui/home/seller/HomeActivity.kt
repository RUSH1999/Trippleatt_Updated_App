package com.tripplleat.trippleattcustomer.ui.home.seller

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.ui.auth.business.busi_AuthActivity
import com.tripplleat.trippleattcustomer.ui.auth.customer.AuthActivity
import kotlinx.android.synthetic.main.activity_main.*
/*
* This the main activity which feed all fragments inside it
*/
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var coordinatorLayout : CoordinatorLayout
    lateinit var navigationView : NavigationView
    lateinit var drawerLayout : DrawerLayout
    lateinit var firebaseAuth: FirebaseAuth
    private  var toggle:ActionBarDrawerToggle?=null
    lateinit var pref1:SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*toolbar = findViewById(R.id.toolbar)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        navigationView = findViewById(R.id.navigationView)
        drawerLayout = findViewById(R.id.homeDrawerLayout)
        firebaseAuth = FirebaseAuth.getInstance()
        setupToolBar()*/
        pref1=getSharedPreferences("TP1", Context.MODE_PRIVATE)
        //val name1=pref1.getString("Name1","")
        //Toast.makeText(this, name1, Toast.LENGTH_SHORT).show()

        val navigationView: NavigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        toggle = ActionBarDrawerToggle(
            this,
            homeDrawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle!!.syncState()
    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
       val id = item.itemId
        if (id == android.R.id.home)
            drawerLayout.openDrawer(GravityCompat.START)

        return super.onOptionsItemSelected(item)
    }
    fun setupToolBar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Trippleatt"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.busi_logout -> {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle(Html.fromHtml("<font color='#00BFFF'>Logout</font>"))
                // alertDialogBuilder.setIcon(R.drawable.exit)
                alertDialogBuilder.setMessage("Are you sure sure want to LOGOUT?")
                alertDialogBuilder.setCancelable(false)
                alertDialogBuilder.setPositiveButton(
                    Html.fromHtml("<font color='#00BFFF'>Yes</font>"),
                    DialogInterface.OnClickListener { dialog, which ->

                        val editor1:SharedPreferences.Editor=pref1.edit()
                        editor1.clear()
                        editor1.apply()

                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this@HomeActivity, busi_AuthActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    })
                alertDialogBuilder.setNegativeButton(
                    Html.fromHtml("<font color='#00BFFF'>No</font>"),
                    DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(
                            this@HomeActivity,
                            "You clicked cancel",
                            Toast.LENGTH_LONG
                        ).show()
                    })
                val alertDialog: AlertDialog = alertDialogBuilder.create()
                alertDialog.show()

            }
            R.id.busi_name->{
                //lateinit var pref1:SharedPreferences
                //pref1=getSharedPreferences("TP1", Context.MODE_PRIVATE)
                val name1=pref1.getString("Name1","")
                Toast.makeText(this@HomeActivity, name1, Toast.LENGTH_LONG).show()
            }
            R.id.myProfileFragment->{
                findNavController(R.id.homeActivitynavHostFragment).navigate(R.id.myProfileFragment)
            }

        }
        homeDrawerLayout!!.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onBackPressed() {
        if (homeDrawerLayout!!.isDrawerOpen(GravityCompat.START)) {
            homeDrawerLayout!!.closeDrawer(GravityCompat.START)
        } else {
            val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            alertDialogBuilder.setCancelable(false)
            alertDialogBuilder.setTitle(Html.fromHtml("<font color='#00BFFF'>Confirm Exit...!</font>"))
            // alertDialogBuilder.setIcon(R.drawable.exit)
            alertDialogBuilder.setMessage("Are you sure sure want to exit..?")
            alertDialogBuilder.setPositiveButton(
                Html.fromHtml("<font color='#00BFFF'>Yes</font>")
            ) { dialog, which ->
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                startActivity(intent)
            }
            alertDialogBuilder.setNegativeButton(
                Html.fromHtml("<font color='#00BFFF'>No</font>")
            ) { dialog, which ->
                Toast.makeText(this@HomeActivity, "You clicked cancel", Toast.LENGTH_LONG).show()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }


}
