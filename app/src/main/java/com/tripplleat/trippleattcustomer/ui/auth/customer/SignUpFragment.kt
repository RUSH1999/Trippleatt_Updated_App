package com.tripplleat.trippleattcustomer.ui.auth.customer

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.FragmentSignUpBinding
import com.tripplleat.trippleattcustomer.ui.auth.listeners.fireUploadListener
import com.tripplleat.trippleattcustomer.ui.auth.viewmodel.AuthViewModal
import com.tripplleat.trippleattcustomer.ui.home.customer.activity.Customer_Home
import com.tripplleat.trippleattcustomer.util.GpsUtils
import com.tripplleat.trippleattcustomer.util.toast
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

/**
 * A simple [Fragment] subclass.
 */
class SignUpFragment : Fragment() ,KodeinAware, fireUploadListener {

    private var isGPSEnabled = false
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: AuthViewModelFactory by instance()
    private var viewModal: AuthViewModal?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentSignUpBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_sign_up,container,false)
       viewModal = ViewModelProvider(requireActivity(),factory).get(AuthViewModal::class.java)
binding.viewmodel=viewModal
        viewModal?.userType="CUSTOMER"
viewModal?.fireUploadListener=this
        GpsUtils(requireContext()).turnGPSOn(object : GpsUtils.OnGpsListener {

            override fun gpsStatus(isGPSEnable: Boolean) {
                this@SignUpFragment.isGPSEnabled= isGPSEnable
            }
        })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        invokeLocationAction()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        }
    }
    private fun invokeLocationAction() {
        when {
            !isGPSEnabled ->
                return

            isPermissionsGranted() -> startLocationUpdate()

            shouldShowRequestPermissionRationale() ->
                context?.toast(getString(R.string.permission_request))

            else -> ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_REQUEST
            )
        }
    }

    private fun startLocationUpdate() {
        viewModal?.getLocationData()?.observe(viewLifecycleOwner, Observer {
viewModal?.setLocation(it.latitude,it.longitude)
        })
    }


    private fun isPermissionsGranted() =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

    private fun shouldShowRequestPermissionRationale() =
        ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) && ActivityCompat.shouldShowRequestPermissionRationale(
            requireActivity(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }

    override fun onSuccess() {
        context?.toast("Data Uploaded Sucessfully")
        Intent(requireContext(), AuthActivity::class.java).also {
            it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    override fun onFailed(message: String) {
      context?.toast(message)
    }
    override fun onuploadStarted() {

    }

}

const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101