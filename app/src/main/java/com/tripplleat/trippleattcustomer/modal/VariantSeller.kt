package com.tripplleat.trippleattcustomer.modal

/**
 * This is a model which contain the details of a variant which is related to seller and variant is packed
 */

data class VariantSeller(var published : Boolean,var sellingPrice : String, var Stock : String, var vairantId : String) {

    constructor():this(true,"","","")
}