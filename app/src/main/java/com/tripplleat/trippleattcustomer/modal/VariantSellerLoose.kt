package com.tripplleat.trippleattcustomer.modal

/**
 * This is a modal which contain the details of the loose variant present inside the seller database
 */

 data class VariantSellerLoose(var published : Boolean,var sellingPrice : String, var Stock : String, var sellingUnit : String , var vairantId : String) {
     constructor():this(true,"","","","")
}