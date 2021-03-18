package com.tripplleat.trippleattcustomer.ui.home.seller.listeners

import android.content.Intent

interface ActivityForResultListener {

    fun StartActivityForResult(intent: Intent?, requestCode : Int)
}