package com.tripplleat.trippleattcustomer.ui.auth.business

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.FragmentBusiLoginBinding
import com.tripplleat.trippleattcustomer.databinding.FragmentLoginBinding
import com.tripplleat.trippleattcustomer.ui.auth.customer.AuthViewModelFactory
import com.tripplleat.trippleattcustomer.ui.auth.listeners.AuthListener
import com.tripplleat.trippleattcustomer.ui.auth.viewmodel.AuthViewModal
import com.tripplleat.trippleattcustomer.util.showDialog
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


class busiLoginFragment : Fragment(),KodeinAware,AuthListener {
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: AuthViewModelFactory by instance()
    var pd : Dialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentBusiLoginBinding =
            DataBindingUtil.inflate(inflater,R.layout.fragment_busi_login,container,false)
        val  viewModal= ViewModelProvider(requireActivity(), factory).get(AuthViewModal::class.java)
        binding.viewmodel=viewModal
        viewModal.userType="BUSINESS"
        viewModal.authListener = this
        pd = context?.showDialog("please wait")

        val liveData : LiveData<String> = viewModal._otpSend

        liveData.observe(viewLifecycleOwner, Observer {
            view?.findNavController()?.navigate(R.id.busiOtpVerification)
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



}