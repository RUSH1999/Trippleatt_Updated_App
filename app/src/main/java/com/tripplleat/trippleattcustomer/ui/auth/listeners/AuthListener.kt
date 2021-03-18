package com.tripplleat.trippleattcustomer.ui.auth.listeners

interface AuthListener {
fun onAuthStart()
    fun onCodeSent()
    fun onSuccess()
    fun onFailure(message : String)

}