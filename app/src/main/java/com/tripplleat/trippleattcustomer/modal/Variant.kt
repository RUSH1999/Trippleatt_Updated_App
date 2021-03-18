package com.tripplleat.trippleattcustomer.modal

/**
 * This is a modal which store the details of products variant which is packed
 */

data class Variant(var barcode : String = "", var category : String = "", var id : String = "", var image : String = "", var mrp : String = "", var productName : String = "", var variant : String = "") {

    override fun toString(): String {
        return "$productName - $variant"
    }
}