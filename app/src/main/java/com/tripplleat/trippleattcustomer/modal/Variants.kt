package com.tripplleat.trippleattcustomer.modal

/**
 * main model which contain all variants detail of product which present inside a particular seller database
 */

data class Variants(var productName : String,
                    var category : String,
                    var variantList : List<VariantSeller>,
                    var variantListLoose : List<VariantSellerLoose>) {
    constructor() : this("","", emptyList(), emptyList())
}