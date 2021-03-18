package com.tripplleat.trippleattcustomer.ui.auth.driver

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Scene
import androidx.transition.TransitionManager
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.DriverDetailsBinding
import com.tripplleat.trippleattcustomer.databinding.FragmentDriverSignUpBinding
import com.tripplleat.trippleattcustomer.databinding.SelectvihicleBinding
import com.tripplleat.trippleattcustomer.databinding.VehicleinfoBinding
import com.tripplleat.trippleattcustomer.ui.auth.customer.AuthViewModelFactory
import com.tripplleat.trippleattcustomer.ui.auth.viewmodel.AuthViewModal
import com.tripplleat.trippleattcustomer.ui.home.seller.HomeActivity
import com.tripplleat.trippleattcustomer.util.GpsUtils
import com.tripplleat.trippleattcustomer.util.showDialog
import com.tripplleat.trippleattcustomer.util.toast
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.net.URI


/**
 * A simple [Fragment] subclass.
 * Use the [driverSignUp.newInstance] factory method to
 * create an instance of this fragment.
 */
class driverSignUp : Fragment(),KodeinAware ,driverDatUplaodListener{
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: AuthViewModelFactory by instance()
    private var isGPSEnabled = false
var viewGroup:ViewGroup?=null
    var vehicleselect:Scene?=null
    var driverInfo:Scene?=null
    var vehicleinfo:Scene?=null
    var viewModal:AuthViewModal?=null
    var binding: FragmentDriverSignUpBinding?=null
    var filePath:Uri?=null
    var pd:Dialog?=null
    private val PICK_IMAGE_REQUEST = 71
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
         binding = DataBindingUtil.inflate(
             inflater,
             R.layout.fragment_driver_sign_up,
             container,
             false
         )
       viewModal = ViewModelProvider(requireActivity(), factory).get(AuthViewModal::class.java)
        binding?.viewmodel= viewModal
        viewModal?.driverDatUplaodListener=this
        GpsUtils(requireContext()).turnGPSOn(object : GpsUtils.OnGpsListener {

            override fun gpsStatus(isGPSEnable: Boolean) {
                this@driverSignUp.isGPSEnabled= isGPSEnable
            }
        })
        pd=context?.showDialog("Please wail we are uploading your data...")
        viewGroup=binding?.rootcontainer as ViewGroup
        driverInfo= Scene.getSceneForLayout(
            binding?.rootcontainer!!, R.layout.driver_details,
            requireContext()
        )
        TransitionManager.go(driverInfo!!)
        val bind1:DriverDetailsBinding = DriverDetailsBinding.bind(viewGroup?.getChildAt(0)!!)
        bind1.viewmodel= viewModal

        return binding?.root
    }

    override fun onVehicleRegistrationStarted() {
       pd?.show()
    }

    override fun onRegistrationCompleted() {
        vehicleselect= Scene.getSceneForLayout(
            binding?.rootcontainer!!, R.layout.selectvihicle,
            requireContext()
        )
        TransitionManager.go(vehicleselect!!)
        val bind1:SelectvihicleBinding = SelectvihicleBinding.bind(viewGroup?.getChildAt(0)!!)
        bind1.viewmodel= viewModal
    }

    override fun onVihicleSelectedCompleted() {
        vehicleinfo= Scene.getSceneForLayout(
            binding?.rootcontainer!!, R.layout.vehicleinfo,
            requireContext()
        )
        vehicleinfo?.enter()
        val bind1:VehicleinfoBinding = VehicleinfoBinding.bind(viewGroup?.getChildAt(0)!!)
        bind1.viewmodel= viewModal
        viewModal?.multiDeleveryBoolean?.value=true
        bind1.sayyes.setOnClickListener(View.OnClickListener {
            bind1.sayno.setBackgroundResource(R.drawable.switchsecondary)
            bind1.sayyes.setBackgroundResource(R.drawable.switchprimary)
            bind1.sayno.setTextColor(Color.BLACK)
            bind1.sayyes.setTextColor(Color.WHITE)
            viewModal?.multiDeleveryBoolean?.value=true
        })
        bind1.sayno.setOnClickListener(View.OnClickListener {
            bind1.sayyes.setBackgroundResource(R.drawable.switchsecondary)
            bind1.sayno.setBackgroundResource(R.drawable.switchprimary)
            bind1.sayyes.setTextColor(Color.BLACK)
            bind1.sayno.setTextColor(Color.WHITE)
            viewModal?.multiDeleveryBoolean?.value=false
        })
        bind1.upload.setOnClickListener(View.OnClickListener {
            val intent=Intent()
            intent.type="image/*"
            intent.action=Intent.ACTION_GET_CONTENT
            startActivityForResult(intent,PICK_IMAGE_REQUEST)
        })
viewModal?.driverDatUplaodListener=this


    }

    override fun onVehicleinfoCompleted() {
        pd?.dismiss()
        Intent(requireContext(), HomeActivity::class.java).also {
            it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    override fun onFailed(message: String) {
        pd?.dismiss()
        Log.d("data", message)
       context?.toast(message)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
            && data != null && data.getData() != null )
        {
            viewModal?.uriImage?.value=data.data
        }
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == com.tripplleat.trippleattcustomer.ui.auth.customer.GPS_REQUEST) {
                isGPSEnabled = true
                invokeLocationAction()
            }
        }
    }
    override fun onStart() {
        super.onStart()
        invokeLocationAction()
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
                com.tripplleat.trippleattcustomer.ui.auth.customer.LOCATION_REQUEST
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
            com.tripplleat.trippleattcustomer.ui.auth.customer.LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }
}
const val LOCATION_REQUEST = 100
const val GPS_REQUEST = 101