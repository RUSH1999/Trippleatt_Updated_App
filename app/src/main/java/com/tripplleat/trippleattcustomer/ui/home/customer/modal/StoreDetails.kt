package com.tripplleat.trippleattcustomer.ui.home.customer.modal

import com.google.firebase.firestore.GeoPoint

/**
 * This is a modal which store the details of shop which is used by the adapter
 */

class StoreDetails(var address : String = "", var category : String = "", var contact : String = "", var email : String = "",
                    var gst : String = "",
                   var id : String = "",
                    var image1 : String = "",
                    var image2 : String = "",
                    var image3 : String = "",
                    var location : GeoPoint = GeoPoint(0.0,0.0),
                    var status : Boolean = true,
                    var pincode : Int = 0,
                    var rating : Double = 0.0,
                    var ratingCounts : Int = 0,
                    var shopName : String = "",
                    var subCategory : String = "")    {
}