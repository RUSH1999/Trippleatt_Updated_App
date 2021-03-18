package com.tripplleat.trippleattcustomer.ui.auth.customer


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.ui.home.customer.activity.Customer_Home
import com.tripplleat.trippleattcustomer.ui.home.seller.HomeActivity


class AuthActivity : AppCompatActivity() {

    lateinit var sp: SharedPreferences
    var isRemember= false
    lateinit var sp1: SharedPreferences
    var isRemember1 = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        /*val user = FirebaseAuth.getInstance().currentUser
        if(user != null){
            startActivity(Intent(this, Customer_Home::class.java))
        }*/

        sp=getSharedPreferences("TP", Context.MODE_PRIVATE)
        isRemember=sp.getBoolean("CB",false)
        if(isRemember) {
            Toast.makeText(this, "Customer", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, Customer_Home::class.java))
        }


        sp1=getSharedPreferences("TP1", Context.MODE_PRIVATE)
        isRemember1=sp1.getBoolean("CB1",false)
        if(isRemember1) {
            Toast.makeText(this, "Business", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }
}
