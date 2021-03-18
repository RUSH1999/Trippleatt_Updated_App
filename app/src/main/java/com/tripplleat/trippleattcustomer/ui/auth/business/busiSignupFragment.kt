package com.tripplleat.trippleattcustomer.ui.auth.business

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.FragmentBusiSignupBinding
import com.tripplleat.trippleattcustomer.databinding.FragmentSignUpBinding
import com.tripplleat.trippleattcustomer.ui.auth.customer.AuthViewModelFactory
import com.tripplleat.trippleattcustomer.ui.auth.listeners.fireUploadListener
import com.tripplleat.trippleattcustomer.ui.auth.viewmodel.AuthViewModal
import com.tripplleat.trippleattcustomer.util.GpsUtils
import com.tripplleat.trippleattcustomer.util.toast
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class busiSignupFragment : Fragment(),KodeinAware,fireUploadListener {

    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: AuthViewModelFactory by instance()
    private var viewModal: AuthViewModal?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentBusiSignupBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_busi_signup,container,false)
        viewModal = ViewModelProvider(requireActivity(),factory).get(AuthViewModal::class.java)
        binding.viewmodel=viewModal
        viewModal?.userType="CUSTOMER"
        viewModal?.fireUploadListener=this

        return binding.root
    }

    override fun onSuccess() {
        context?.toast("Data Uploaded Sucessfully")
        view?.findNavController()?.navigate(R.id.busimap)
    }

    override fun onFailed(message: String) {
        context?.toast(message)
    }
    override fun onuploadStarted() {

    }


}