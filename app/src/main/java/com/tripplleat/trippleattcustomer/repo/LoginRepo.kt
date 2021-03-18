package com.tripplleat.trippleattcustomer.repo

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.tripplleat.trippleattcustomer.modal.UserInfo
import com.tripplleat.trippleattcustomer.modal.Vehicleinfo
import com.tripplleat.trippleattcustomer.network.myApi
import com.tripplleat.trippleattcustomer.ui.auth.business.jsonListener
import com.tripplleat.trippleattcustomer.ui.auth.driver.driverDatUplaodListener
import com.tripplleat.trippleattcustomer.ui.auth.listeners.fireUploadListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.imperiumlabs.geofirestore.GeoFirestore
import org.imperiumlabs.geofirestore.extension.setLocation

class LoginRepo() {
     var bool : Boolean = false
     val auth = FirebaseAuth.getInstance()
     val uid = auth.currentUser?.uid
     val CUST_DATABASE = FirebaseFirestore.getInstance().collection("customer/user/profile")
    val BUSINESS_DATABASE = FirebaseFirestore.getInstance().collection("business/user/profile")
    val DRIVER_DATABASE = FirebaseFirestore.getInstance().collection("driver/user/profile")
    val DRIVER_IMAGE_FIRESTORAGE = FirebaseStorage.getInstance().getReference().child("drivers")

  suspend fun isUserRegistered(
      phone_number: String,
      userData: MutableLiveData<Int>,
      userType: String
  ){
    var mode: Int = 0
     val job =  GlobalScope.launch {
         when(userType){
             "BUSINESS" ->
                 BUSINESS_DATABASE.document(uid.toString())
                     .get().addOnSuccessListener { document ->
                         Log.i("database", "${document.exists()}")
                         Log.i("databaseUID", "${uid}")
                         if (document.exists() && document.contains("mobile_number")) {
                             mode = 1
                             userData.value = mode;
                         } else {
                             mode = 2;
                             userData.value = mode;
                         }
                     }.addOnFailureListener { exception ->
                         Log.i("database", "${exception.message}")
                     }.await()
             "CUSTOMER" ->
                 CUST_DATABASE.document(uid.toString())
                     .get().addOnSuccessListener { document ->
                         Log.i("database", "${document.exists()}")
                         Log.i("databaseUID", "${uid}")
                         if (document.exists() && document.contains("mobile_number")) {
                             mode = 1
                             userData.value = mode;
                         } else {
                             mode = 2;
                             userData.value = mode;
                         }
                     }.addOnFailureListener { exception ->
                         Log.i("database", "${exception.message}")
                     }.await()
             "DRIVER" ->
                 DRIVER_DATABASE.document(uid.toString())
                     .get().addOnSuccessListener { document ->
                         Log.i("database", "${document.exists()}")
                         Log.i("databaseUID", "${uid}")
                         if (document.exists() && document.contains("licenseImage") && document.contains("mobile_number")) {
                             mode = 1
                             userData.value = mode;
                         } else {
                             mode = 2;
                             userData.value = mode;
                         }
                     }.addOnFailureListener { exception ->
                         Log.i("database", "${exception.message}")
                     }.await()
         }

      }
        job.join()

  }
    fun registerAsDriver(userInfo: UserInfo, listener: driverDatUplaodListener){
        val  documentReference=DRIVER_DATABASE.document(uid.toString())
        documentReference.set(userInfo).addOnCompleteListener {
            if(it.isSuccessful){
                listener.onRegistrationCompleted()

            }else{
                listener.onFailed("Data not saved: " + it.exception?.message.toString())
            }
        }

    }
    fun registerAsBusiness(userInfo: UserInfo, phone_number: String, listener: fireUploadListener){
        val  documentReference=BUSINESS_DATABASE.document(uid.toString())
        documentReference.set(userInfo).addOnCompleteListener {
            if(it.isSuccessful){
                listener.onSuccess()

            }else{
                listener.onFailed("Data not saved: " + it.exception?.message.toString())
            }
        }

    }

  fun register(
      userInfo: UserInfo,
      phone_number: String,
      geoPoint: GeoPoint,
      listener: fireUploadListener
  ){
   val documentRefrence = CUST_DATABASE.document(uid.toString())
   documentRefrence.set(userInfo).addOnCompleteListener {
    if(it.isSuccessful){
        val geoFirestore = GeoFirestore(CUST_DATABASE)
        geoFirestore.setLocation(uid.toString(), geoPoint){ exception->
if (exception!=null){
listener.onFailed("Your location not uploaded: " + exception.message.toString())
}else{
    listener.onSuccess()
}
        }
   }else{
    listener.onFailed("Data not saved: " + it.exception?.message.toString())
    }
   }
  }
    fun getSearchPlaceFromApi(url: String, listener: jsonListener, ctx: Context){
val myApi=myApi(ctx, listener, url)
        myApi.Jsonparse()
    }
fun registerLocationForBusiness(geoPoint: GeoPoint, fireUploadListener: fireUploadListener,formated_adress:String){
 val   data=HashMap<String, Any>()
    data.put("formated_address",formated_adress)
    BUSINESS_DATABASE.document(uid.toString()).set(data, SetOptions.merge()).addOnSuccessListener {
        val geoFirestore = GeoFirestore(BUSINESS_DATABASE)
        geoFirestore.setLocation(uid.toString(), geoPoint){ exception->
            if (exception!=null){
                fireUploadListener.onFailed("Your location not uploaded: " + exception.message.toString())
            }else{

                fireUploadListener.onSuccess()
            }
        }
    }.addOnFailureListener(OnFailureListener {
        fireUploadListener.onFailed("Your location not uploaded: " + it.message.toString())
    })

}
    fun vehiInfoRegistered(
        vehicleinfo: Vehicleinfo,
        driverDatUplaodListener: driverDatUplaodListener,
        uri: Uri,
        geoPoint: GeoPoint
    ){
        DRIVER_DATABASE.document(uid.toString()).set(vehicleinfo, SetOptions.merge())
        driverDatUplaodListener.onVehicleRegistrationStarted()
        val ImageRef = DRIVER_IMAGE_FIRESTORAGE.child("prof_pic").child(uid.toString()).child(
            uri.getLastPathSegment().toString()
        );
        val geoFirestore = GeoFirestore(DRIVER_DATABASE)
        geoFirestore.setLocation(uid.toString(), geoPoint){ exception->
            if (exception!=null){
                driverDatUplaodListener.onFailed("Your location not uploaded: " + exception.message.toString())
            }else{
                ImageRef.putFile(uri).addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful) {
                        ImageRef.downloadUrl.addOnSuccessListener {
                            val image: MutableMap<String, Any> = HashMap()
                            image["licenseImage"] = it.toString()
                            DRIVER_DATABASE.document(uid.toString()).set(image, SetOptions.merge()).addOnCompleteListener(
                                OnCompleteListener {
                                    if (it.isSuccessful){
                                        driverDatUplaodListener.onVehicleinfoCompleted()
                                    }else{
                                        driverDatUplaodListener.onFailed(it.exception
                                            ?.message.toString())
                                    }
                                }
                            )
                        }
                    } else {
                        driverDatUplaodListener.onFailed(it.exception?.message.toString())
                    }
                })
            }
        }


    }
}