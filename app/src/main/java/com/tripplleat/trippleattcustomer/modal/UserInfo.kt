package com.tripplleat.trippleattcustomer.modal

data class UserInfo(val first_name : String, val last_name : String, val mobile_number : String) {
}
data class Vehicleinfo(val vehicleType : String, val regiNumber : String, val vehiCompanyName : String,
                       val vehiModelName : String, val vehiLoadCap : String , val multidelevery : Boolean) {
}