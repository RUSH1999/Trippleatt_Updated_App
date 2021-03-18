package com.tripplleat.trippleattcustomer.ui.auth.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.TaskExecutors
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.GeoPoint
import com.tripplleat.trippleattcustomer.modal.UserInfo
import com.tripplleat.trippleattcustomer.modal.Vehicleinfo
import com.tripplleat.trippleattcustomer.repo.LoginRepo
import com.tripplleat.trippleattcustomer.ui.auth.business.busi_AuthActivity
import com.tripplleat.trippleattcustomer.ui.auth.business.jsonListener
import com.tripplleat.trippleattcustomer.ui.auth.customer.AuthActivity
import com.tripplleat.trippleattcustomer.ui.auth.driver.driverAuthActivity
import com.tripplleat.trippleattcustomer.ui.auth.driver.driverDatUplaodListener
import com.tripplleat.trippleattcustomer.ui.auth.listeners.AuthListener
import com.tripplleat.trippleattcustomer.ui.auth.listeners.Otplistener
import com.tripplleat.trippleattcustomer.ui.auth.listeners.fireUploadListener
import com.tripplleat.trippleattcustomer.util.LocationLiveData
import com.tripplleat.trippleattcustomer.util.toast
import kotlinx.coroutines.launch

import java.util.concurrent.TimeUnit


class AuthViewModal(private val repo: LoginRepo,private val application: Application) : ViewModel() {

    val firebaseAuth = FirebaseAuth.getInstance()
    val userData = MutableLiveData<Int>()
    var _otpSend = MutableLiveData<String>()
    var formated_address= MutableLiveData<String>()
    var multiDeleveryBoolean = MutableLiveData<Boolean>()
    var vehicleValidationError = MutableLiveData<String>()
    var uriImage = MutableLiveData<Uri>()
    var userType :String?=null
    var countdown = MutableLiveData<Long>()
    var phone = MutableLiveData<String>()
    var vehicleType = MutableLiveData<String>()
    var geoloc=MutableLiveData<GeoPoint>()
    var _token = MutableLiveData<PhoneAuthProvider.ForceResendingToken>()
    var contdownfinish = MutableLiveData<Boolean>()
    var phonenumber:String?=null
    var otp1:String?=null
    var otp2:String?=null
    var otp3:String?=null
    var otp4:String?=null
    var otp5:String?=null
    var otp6:String?=null
    var vehicleRegisNumber:String?=null
    var vehicleCompanyName:String?=null
    var vehicleModelNumber:String?=null
    var vehicleLoadCap:String?=null
    var firstname:String?=null
    var lastname:String?=null
    var authListener : AuthListener? = null
    var otplistener: Otplistener?=null
    var fireUploadListener: fireUploadListener?=null
    var  jsonListener:jsonListener?=null
    var  driverDatUplaodListener:driverDatUplaodListener?=null
    private val locationData = LocationLiveData(application)
    var mapkey:String?=null
    var query:String?=null

    fun getLocationData() = locationData
    fun getCurrentUser()=FirebaseAuth.getInstance().currentUser
    fun setUserData(mode: Int){
        userData.value = mode
    }

    public  fun sendVerifiactionCode(view: View){
        authListener?.onAuthStart()
        if (phonenumber.isNullOrEmpty()||phonenumber?.length!!<10){
            authListener?.onFailure("Invalid Phone Number")
            return
        }
        phone.value=phonenumber
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91$phonenumber",
            60,
            TimeUnit.SECONDS,
            TaskExecutors.MAIN_THREAD,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    val job = viewModelScope.launch {
                        repo.isUserRegistered(
                            firebaseAuth.currentUser?.phoneNumber.toString(),
                            userData,userType!!
                        )
                        Log.i("databaseModel", userData.value.toString())
                        authListener?.onSuccess()
                    }
                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    authListener?.onFailure(p0.toString())
                    contdownfinish.value=true
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    authListener?.onCodeSent()
                    _otpSend.value = p0
                    _token.value = p1
                    otplistener?.onOtpcountstarted()
                    contdownfinish.value=false
                    val timer = object : CountDownTimer(60000, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            val sec = millisUntilFinished / 1000
                            countdown.value = sec
                        }

                        override fun onFinish() {
                            otplistener?.onOTPtimeout()
                            contdownfinish.value=true
                        }
                    }
                    timer.start()

                }
            })
    }

    fun verifyOtpClicked(view: View){
        if (otp1.isNullOrEmpty()||otp2.isNullOrEmpty()||otp3.isNullOrEmpty()||otp4.isNullOrEmpty()||otp5.isNullOrEmpty()||otp6.isNullOrEmpty()){
            authListener?.onFailure("Invalid OTP")
            return
        }
        val code = _otpSend.value.toString()
        Log.i("code", code)
        val credential = PhoneAuthProvider.getCredential(
            code,
            otp1 + otp2 + otp3 + otp4 + otp5 + otp6
        )
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                viewModelScope.launch {
                    repo.isUserRegistered(
                         firebaseAuth.currentUser?.phoneNumber.toString(),
                         userData,userType!!
                     )
                }
                authListener?.onSuccess()
            }
            else{
                authListener?.onFailure("Verification failed")
                //Toast.makeText(application,"Otp verification failed ${it.exception?.message}",Toast.LENGTH_SHORT).show()
            }
        }
    }
 fun registerBusiness(view: View){
    Log.d("registeruser","From register business")
    if (firstname.isNullOrEmpty()||lastname.isNullOrEmpty()){
        fireUploadListener?.onFailed("Enter your first and last name")
    }
    val user = UserInfo(
        firstname!!,
        lastname!!,
        firebaseAuth.currentUser?.phoneNumber.toString()
    )
    viewModelScope.launch {

        repo.registerAsBusiness(user, firebaseAuth.currentUser?.phoneNumber.toString(),fireUploadListener!!)


    }
}
    fun registerUser(view: View){
        Log.d("registeruser","From register user")
        if (firstname.isNullOrEmpty()||lastname.isNullOrEmpty()){
            fireUploadListener?.onFailed("Enter your first and last name")
            return
        }
        val user = UserInfo(
            firstname!!,
            lastname!!,
            firebaseAuth.currentUser?.phoneNumber.toString()
        )
        viewModelScope.launch {
            if (geoloc.value!=null){
                repo.register(user, firebaseAuth.currentUser?.phoneNumber.toString(),geoloc.value!!,fireUploadListener!!)
            }else{
                fireUploadListener?.onFailed("Location not Found")
            }

        }
        }
fun resendCode(view: View){
if (!contdownfinish.value!!){
    return
}
    PhoneAuthProvider.getInstance().verifyPhoneNumber("+91${phone.value}",
        60,
        TimeUnit.SECONDS,
        TaskExecutors.MAIN_THREAD,
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                val job = viewModelScope.launch {
                    repo.isUserRegistered(
                        firebaseAuth.currentUser?.phoneNumber.toString(),
                        userData,userType!!
                    )
                    Log.i("databaseModel", userData.value.toString())
                    authListener?.onSuccess()
                }
            }

            override fun onVerificationFailed(p0: FirebaseException) {
                authListener?.onFailure(p0.toString())
            }

            override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                super.onCodeSent(p0, p1)
                authListener?.onCodeSent()
                _otpSend.value = p0
                _token.value = p1
                otplistener?.onOtpcountstarted()
                contdownfinish.value=false
                val timer = object : CountDownTimer(60000, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        val sec = millisUntilFinished / 1000
                        countdown.value = sec
                    }

                    override fun onFinish() {
                        otplistener?.onOTPtimeout()
                        contdownfinish.value=true
                    }
                }
                timer.start()

            }
        },_token.value)
}
    fun setLocation(lati:Double,longi:Double){
geoloc.value= GeoPoint(lati,longi)
        Log.d("dataloc","lati: ${lati} longi: ${longi}")
    }

    fun gotoBusilogin(view: View){
        val intent= Intent(view.context,busi_AuthActivity::class.java)
        view.context.startActivity(intent)
    }

    fun gotoCustlogin(view: View){
        val intent= Intent(view.context,AuthActivity::class.java)
        view.context.startActivity(intent)
    }

    fun gotoDriverlogin(view: View){
        val intent= Intent(view.context,driverAuthActivity::class.java)
        view.context.startActivity(intent)
    }

fun searchQuery(view: View){
    if (query.isNullOrEmpty()){
        jsonListener?.onFailed("Please Type your address to continue")
        return
    }
    val url:String="https://maps.googleapis.com/maps/api/place/textsearch/json?query="+query+"&type=store&key="+mapkey
    repo.getSearchPlaceFromApi(url,jsonListener!!,view.context)
}
    fun setLocationForBusiness(view: View){
        if (geoloc.value==null){
            fireUploadListener?.onFailed("Location failed")
            return
        }
        repo.registerLocationForBusiness(geoloc.value!!,fireUploadListener!!,formated_address.value!!)
    }
    fun registerDriver(view: View){
        Log.d("registeruser","From register driver")
        if (firstname.isNullOrEmpty()||lastname.isNullOrEmpty()){
            driverDatUplaodListener?.onFailed("Enter your first and last name")
            return
        }
        val user = UserInfo(
            firstname!!,
            lastname!!,
            firebaseAuth.currentUser?.phoneNumber.toString()
        )
        viewModelScope.launch {

            repo.registerAsDriver(user,driverDatUplaodListener!!)


        }
    }
    fun registerVehicletype(view: View){
        vehicleType.value=view.tag.toString()
        driverDatUplaodListener?.onVihicleSelectedCompleted()
    }
    fun changeVihicleType(view: View){
        vehicleType.value=view.tag.toString()
    }

    fun vehicleInfoRegister(view: View){
        viewModelScope.launch {
            if (!getVehicleValidation()) {
                Log.d("failed","failed")
                driverDatUplaodListener?.onFailed(vehicleValidationError.value!!)

            } else if (geoloc.value == null) {
                    driverDatUplaodListener?.onFailed("Location failed")

                } else {
                    val vehiinfo = Vehicleinfo(
                        vehicleType.value!!,
                        vehicleRegisNumber!!,
                        vehicleCompanyName!!,
                        vehicleModelNumber!!,
                        vehicleLoadCap!!,
                        multiDeleveryBoolean.value!!

                    )
                    repo.vehiInfoRegistered(
                        vehiinfo,
                        driverDatUplaodListener!!,
                        uriImage.value!!,
                        geoloc.value!!
                    )
                }
        }


    }

    fun getVehicleValidation():Boolean{
        var validationSuccess:Boolean=false
        if (uriImage.value==null){
            validationSuccess=false
            vehicleValidationError.value="Please upload your driving license"

        }else{
            if (vehicleType.value==null){
                validationSuccess=false
                vehicleValidationError.value="Please select your vehicle first"
            }else{
                if (vehicleRegisNumber.isNullOrEmpty()){
                    validationSuccess=false
                    vehicleValidationError.value="Please enter your vehicle number"
                }else{
                    if (vehicleCompanyName.isNullOrEmpty()){
                        validationSuccess=false
                        vehicleValidationError.value="Please enter your vehicle company name"
                    }else{
                        if (vehicleModelNumber.isNullOrEmpty()){
                            validationSuccess=false
                            vehicleValidationError.value="Please enter your vehicle model name"
                        }else{
                            if (vehicleLoadCap.isNullOrEmpty()){
                                validationSuccess=false
                                vehicleValidationError.value="Please enter your vehicle load capacity in KG"
                            }else{

validationSuccess=true
                            }

                        }

                    }

                }
            }

        }
        return validationSuccess
    }
}