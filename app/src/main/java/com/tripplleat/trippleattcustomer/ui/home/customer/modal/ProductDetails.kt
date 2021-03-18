package com.tripplleat.trippleattcustomer.ui.home.customer.modal

/*
* This is a modal class which contain the product details which is added bu the user in cart
* */

data class ProductDetails(
    var productName: String,
    var variantName: String,
    var image: String,
    var mrp: Int,
    var sellingPrice: Int,
    var orderedQuantity: Int,
    var existedQunatity : Int,
    var variantUnit : String,
    var totalPrice : Int,
    var customerId : String,
    var sellerId : String,
    var productId : String,
    var shopName : String,
    var variantId : String
) {
    constructor() : this("","","",0,0,0,0,"",0,"","","","","")
}