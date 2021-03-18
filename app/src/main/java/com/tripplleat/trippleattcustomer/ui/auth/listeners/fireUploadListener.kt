package com.tripplleat.trippleattcustomer.ui.auth.listeners

interface fireUploadListener{
    fun onuploadStarted()
    fun onSuccess()
    fun onFailed(message:String)
}