package com.tripplleat.trippleattcustomer.ui.auth.business

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.FragmentBusimapBinding
import com.tripplleat.trippleattcustomer.ui.auth.customer.AuthViewModelFactory
import com.tripplleat.trippleattcustomer.ui.auth.listeners.fireUploadListener
import com.tripplleat.trippleattcustomer.ui.auth.viewmodel.AuthViewModal
import com.tripplleat.trippleattcustomer.util.GpsUtils
import com.tripplleat.trippleattcustomer.util.showDialog
import com.tripplleat.trippleattcustomer.util.toast
import org.json.JSONArray
import org.json.JSONObject
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance


/**
 * A simple [Fragment] subclass.
 * Use the [Busimap.newInstance] factory method to
 * create an instance of this fragment.
 */
class Busimap : Fragment(),KodeinAware, OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: AuthViewModelFactory by instance()
    private var viewModal: AuthViewModal?=null
    private var pd:Dialog?=null
    var binding : FragmentBusimapBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_busimap,
            container,
            false
        )
        requireActivity().window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_IMMERSIVE
        requireActivity().window.setBackgroundDrawableResource(R.drawable.main_gradient)
        viewModal = ViewModelProvider(requireActivity(), factory).get(AuthViewModal::class.java)
        binding?.viewmodel = viewModal
        viewModal?.userType = "BUSINESS"
        viewModal?.mapkey = getString(R.string.google_maps_key)
        pd = context?.showDialog("Loading")
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        return binding?.root
    }

    override fun onMapReady(p0: GoogleMap?) {
        val latti:Double= arguments?.getDouble("latti")!!
        val longi:Double= arguments?.getDouble("longi")!!
        val geoPoint=GeoPoint(latti,longi)
        viewModal?.geoloc?.value=geoPoint
        viewModal?.formated_address?.value=arguments?.getString("adress")
        if (p0 != null) {
            mMap=p0
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                mMap.isMyLocationEnabled=true

            val loc = LatLng(latti,longi)
            mMap.addMarker(MarkerOptions().position(loc))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc))
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 15f))
            binding?.changemapview?.setOnClickListener(View.OnClickListener {
                if (mMap.mapType == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                } else {
                    mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                }
            })
            binding?.searchadd?.text=arguments?.getString("adress")
            mMap.setOnMapClickListener { latLng ->
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(latLng))
                viewModal?.setLocation(latLng.latitude, latLng.longitude)

            }
        }
    }
}