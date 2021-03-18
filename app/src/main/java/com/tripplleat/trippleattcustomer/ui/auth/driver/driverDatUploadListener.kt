package com.tripplleat.trippleattcustomer.ui.auth.driver

interface  driverDatUplaodListener{
    fun onVehicleRegistrationStarted()
fun onRegistrationCompleted()
    fun onVihicleSelectedCompleted()
    fun onVehicleinfoCompleted()
    fun onFailed(message:String)
}