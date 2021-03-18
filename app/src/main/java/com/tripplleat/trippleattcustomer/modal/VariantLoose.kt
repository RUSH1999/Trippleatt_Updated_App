package com.tripplleat.trippleattcustomer.modal

/**
 * THis is a modal which stor the details of products variant which is loose
 */

class VariantLoose ( var category : String = "", var id : String = "", var image : String = "", var productName : String = "",var variantUnit : String = ""){
    override fun toString(): String {
        return "$productName - Loose"
    }
}