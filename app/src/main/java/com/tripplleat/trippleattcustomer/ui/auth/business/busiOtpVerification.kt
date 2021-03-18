package com.tripplleat.trippleattcustomer.ui.auth.business

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.BusiverificationFragmentBinding
import com.tripplleat.trippleattcustomer.databinding.FragmentOtpVerificationBinding
import com.tripplleat.trippleattcustomer.ui.auth.customer.AuthViewModelFactory
import com.tripplleat.trippleattcustomer.ui.auth.listeners.AuthListener
import com.tripplleat.trippleattcustomer.ui.auth.listeners.Otplistener
import com.tripplleat.trippleattcustomer.ui.auth.viewmodel.AuthViewModal
import com.tripplleat.trippleattcustomer.ui.home.customer.activity.Customer_Home
import com.tripplleat.trippleattcustomer.ui.home.seller.HomeActivity
import com.tripplleat.trippleattcustomer.util.showDialog
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class busiOtpVerification : Fragment(), AuthListener, KodeinAware, TextWatcher, Otplistener {
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: AuthViewModelFactory by instance()
    var pd : Dialog? = null
    var otp1: EditText?=null
    var otp2: EditText?=null
    var otp3: EditText?=null
    var otp4: EditText?=null
    var otp5: EditText?=null
    var otp6: EditText?=null
    var counter: TextView?=null
    var resendcode: TextView?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding:BusiverificationFragmentBinding  = DataBindingUtil.inflate(inflater,R.layout.busiverification_fragment,container,false)
        val viewModal = ViewModelProvider(requireActivity(),factory).get(AuthViewModal::class.java)
        binding.viewmodel=viewModal
        viewModal.userType="BUSINESS"
        viewModal.authListener = this
        viewModal.otplistener=this
        otp1=binding.otp1
        otp2=binding.otp2
        otp3=binding.otp3
        otp4=binding.otp4
        otp5=binding.otp5
        otp6=binding.otp6
        counter=binding.countdown
        resendcode=binding.resendCode
        otp1?.addTextChangedListener(this)
        otp2?.addTextChangedListener(this)
        otp3?.addTextChangedListener(this)
        otp4?.addTextChangedListener(this)
        otp5?.addTextChangedListener(this)
        otp6?.addTextChangedListener(this)
        pd = context?.showDialog("please wait")

        val liveData : LiveData<Int> = viewModal.userData
        val otpcountdown : LiveData<Long> = viewModal.countdown
        liveData.observe(viewLifecycleOwner, Observer {mode ->
            Log.i("database1","${mode}")
            if(mode == 2) {
                view?.findNavController()?.navigate(R.id.busiSignupFragment)
            }
            if(mode == 1){
                lateinit var sp1: SharedPreferences
                sp1=requireActivity().getSharedPreferences("TP1", Context.MODE_PRIVATE)
                val user1 = FirebaseAuth.getInstance().currentUser
                val name1:String=user1.toString()
                val editor1: SharedPreferences.Editor=sp1.edit()
                editor1.putString("Name1",name1)
                editor1.putBoolean("CB1",true)
                editor1.apply()
                Toast.makeText(context, " business user existed", Toast.LENGTH_SHORT).show()
                startActivity(Intent(context, HomeActivity::class.java))

            }

        })
        otpcountdown.observe(viewLifecycleOwner, Observer {
            binding.countdown.text=it?.toString()
        })

        return binding.root
    }

    override fun onAuthStart() {
        pd?.show()
    }
    override fun onCodeSent() {
        pd?.dismiss()
        Toast.makeText(context,"code sent", Toast.LENGTH_SHORT).show()
    }

    override fun onSuccess() {
        pd?.dismiss()
        Toast.makeText(context,"Verification Syccessfull", Toast.LENGTH_SHORT).show()
    }

    override fun onFailure(message : String) {
        pd?.dismiss()
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(p0: Editable?) {
        if (p0?.length==1){
            if (otp1?.length() == 1) {
                otp2?.requestFocus()

            }
            if (otp2?.length() == 1) {
                otp3?.requestFocus()

            }
            if (otp3?.length() == 1) {
                otp4?.requestFocus()

            }
            if (otp4?.length() == 1) {
                otp5?.requestFocus()

            }
            if (otp5?.length() == 1) {
                otp6?.requestFocus()


            }
        }else if (p0?.length == 0) {
//            if user tries to delete numbers
            if (otp6?.length() == 0) {
                otp5?.requestFocus()
            }
            if (otp5?.length() == 0) {
                otp4?.requestFocus()

            }
            if (otp4?.length() == 0) {
                otp3?.requestFocus()
            }
            if (otp3?.length() == 0) {
                otp2?.requestFocus()
            }
            if (otp2?.length() == 0) {
                otp1?.requestFocus()
            }
        }
        if (otp1?.length()==0){
            otp1?.setBackgroundResource(R.drawable.stroke_otp)
        }else{
            otp1?.setBackgroundResource(R.drawable.stroke_otp_clicked)
        }
        if (otp2?.length()==0){
            otp2?.setBackgroundResource(R.drawable.stroke_otp)
        }else{
            otp2?.setBackgroundResource(R.drawable.stroke_otp_clicked)
        }
        if (otp3?.length()==0){
            otp3?.setBackgroundResource(R.drawable.stroke_otp)
        }else{
            otp3?.setBackgroundResource(R.drawable.stroke_otp_clicked)
        }
        if (otp4?.length()==0){
            otp4?.setBackgroundResource(R.drawable.stroke_otp)
        }else{
            otp4?.setBackgroundResource(R.drawable.stroke_otp_clicked)
        }
        if (otp5?.length()==0){
            otp5?.setBackgroundResource(R.drawable.stroke_otp)
        }else{
            otp5?.setBackgroundResource(R.drawable.stroke_otp_clicked)
        }
        if (otp6?.length()==0){
            otp6?.setBackgroundResource(R.drawable.stroke_otp)
        }else{
            otp6?.setBackgroundResource(R.drawable.stroke_otp_clicked)
        }
    }

    override fun onOtpcountstarted() {
        resendcode?.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
        resendcode?.isClickable=false
    }

    override fun onOTPtimeout() {
        counter?.text=""
        resendcode?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
        resendcode?.isClickable=true
    }

}
