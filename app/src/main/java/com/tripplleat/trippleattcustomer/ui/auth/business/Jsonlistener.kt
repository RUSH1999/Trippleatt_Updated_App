package com.tripplleat.trippleattcustomer.ui.auth.business

import org.json.JSONObject

interface jsonListener{
    fun onStarted()
    fun onfinished(response:JSONObject)
fun onFailed(message:String)
}