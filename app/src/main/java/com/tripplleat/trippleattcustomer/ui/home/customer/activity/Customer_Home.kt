package com.tripplleat.trippleattcustomer.ui.home.customer.activity

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.fragment.app.replace
import androidx.navigation.findNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.ui.auth.customer.AuthActivity
import com.tripplleat.trippleattcustomer.ui.home.customer.fragments.CartFragment
import kotlinx.android.synthetic.main.activity_customer__home.*

/*
* This is the main activity for the customer part of the application
* */

class Customer_Home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var pref:SharedPreferences
    lateinit var imgCart : ImageView
    private  var toggle: ActionBarDrawerToggle?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer__home)
        pref=getSharedPreferences("TP", Context.MODE_PRIVATE)


/*
        imgCart = findViewById(R.id.imgCart)
        imgCart.setOnClickListener {
            findNavController(R.id.customerActivitynavHostFragment).navigate(R.id.cartFragment)
        }

        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        toggle = ActionBarDrawerToggle(
            this,
            Drawer_Layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        toggle!!.syncState()
        */
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.Logout -> {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle(Html.fromHtml("<font color='#00BFFF'>Logout</font>"))
                // alertDialogBuilder.setIcon(R.drawable.exit)
                alertDialogBuilder.setMessage("Are you sure sure want to LOGOUT?")
                alertDialogBuilder.setCancelable(false)
                alertDialogBuilder.setPositiveButton(
                    Html.fromHtml("<font color='#00BFFF'>Yes</font>"),
                    DialogInterface.OnClickListener { dialog, which ->
                        val editor:SharedPreferences.Editor=pref.edit()
                        editor.clear()
                        editor.apply()
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this@Customer_Home, AuthActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    })
                alertDialogBuilder.setNegativeButton(
                    Html.fromHtml("<font color='#00BFFF'>No</font>"),
                    DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(
                            this@Customer_Home,
                            "You clicked cancel",
                            Toast.LENGTH_LONG
                        ).show()
                    })
                val alertDialog: AlertDialog = alertDialogBuilder.create()
                alertDialog.show()

            }
            R.id.name1 -> {
                val name1=pref.getString("Name","")
                Toast.makeText(this@Customer_Home, name1, Toast.LENGTH_SHORT).show()
            }

            R.id.cart -> {
                findNavController(R.id.customerActivitynavHostFragment).navigate(R.id.cartFragment)
            }

            R.id.profile -> {
                findNavController(R.id.customerActivitynavHostFragment).navigate(R.id.pp)
            }

            R.id.order ->{
                findNavController(R.id.customerActivitynavHostFragment).navigate(R.id.order1)
            }

        }
        Drawer_Layout!!.closeDrawer(GravityCompat.START)
        return true
    }
    override fun onBackPressed() {
        if (Drawer_Layout!!.isDrawerOpen(GravityCompat.START)) {
            Drawer_Layout!!.closeDrawer(GravityCompat.START)
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
                Toast.makeText(this@Customer_Home, "You clicked cancel", Toast.LENGTH_LONG).show()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }

}
