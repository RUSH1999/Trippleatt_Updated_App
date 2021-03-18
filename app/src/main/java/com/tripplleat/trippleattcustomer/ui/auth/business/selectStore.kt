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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.GeoPoint
import com.squareup.picasso.Picasso
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.FragmentSelectStoreBinding
import com.tripplleat.trippleattcustomer.ui.auth.customer.AuthViewModelFactory
import com.tripplleat.trippleattcustomer.ui.auth.listeners.fireUploadListener
import com.tripplleat.trippleattcustomer.ui.auth.viewmodel.AuthViewModal
import com.tripplleat.trippleattcustomer.ui.home.seller.HomeActivity
import com.tripplleat.trippleattcustomer.util.GpsUtils
import com.tripplleat.trippleattcustomer.util.showDialog
import com.tripplleat.trippleattcustomer.util.toast
import org.json.JSONArray
import org.json.JSONObject
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class selectStore : Fragment() , KodeinAware, fireUploadListener, OnMapReadyCallback,jsonListener {
    private lateinit var mMap: GoogleMap
    private var isGPSEnabled = false
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: AuthViewModelFactory by instance()
    private var viewModal: AuthViewModal?=null
    private  var latti:Double?=null
    private  var longi:Double?=null
    private var maploadedForFirst:Boolean=false
    private var pd: Dialog?=null
    var binding : FragmentSelectStoreBinding?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding  = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_select_store,
            container,
            false
        )
        requireActivity().window.decorView.systemUiVisibility= View.SYSTEM_UI_FLAG_IMMERSIVE
        requireActivity().window.setBackgroundDrawableResource(R.drawable.main_gradient)
        viewModal = ViewModelProvider(requireActivity(), factory).get(AuthViewModal::class.java)
        binding?.viewmodel=viewModal
        viewModal?.userType="BUSINESS"
        viewModal?.mapkey=getString(R.string.google_maps_key)
        viewModal?.fireUploadListener=this
        pd=context?.showDialog("Loading")
        viewModal?.jsonListener=this
        binding?.search?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                viewModal?.searchQuery(v)
                return@OnKeyListener true
            }
            false
        })
        GpsUtils(requireContext()).turnGPSOn(object : GpsUtils.OnGpsListener {

            override fun gpsStatus(isGPSEnable: Boolean) {
                this@selectStore.isGPSEnabled = isGPSEnable
            }
        })

        return binding?.root

    }
    override fun onStart() {
        super.onStart()
        invokeLocationAction()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == com.tripplleat.trippleattcustomer.ui.auth.customer.GPS_REQUEST) {
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
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                com.tripplleat.trippleattcustomer.ui.auth.customer.LOCATION_REQUEST
            )
        }
    }

    private fun startLocationUpdate() {
        viewModal?.getLocationData()?.observe(viewLifecycleOwner, Observer {
            if (!maploadedForFirst) {
                latti = it.latitude
                longi = it.longitude
                viewModal?.setLocation(it.latitude, it.longitude)
                val mapFragment = childFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
                maploadedForFirst = true
            }
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            com.tripplleat.trippleattcustomer.ui.auth.customer.LOCATION_REQUEST -> {
                invokeLocationAction()
            }
        }
    }

    override fun onuploadStarted() {

    }

    override fun onSuccess() {
        context?.toast("Data Uploaded Sucessfully")
        Intent(requireContext(), HomeActivity::class.java).also {
            it.flags= Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(it)
        }
    }

    override fun onStarted() {
        pd?.show()
    }

    override fun onfinished(response: JSONObject) {
        pd?.dismiss()
        try {
            var ll: LinearLayout ?=null
            val array: JSONArray = response.getJSONArray("results")
            for (i in 0 until array.length()){
                ll=layoutInflater.inflate(
                    R.layout.store_select_design,
                    binding?.container,
                    false
                ) as LinearLayout
                    binding?.container?.addView(ll)
                 val shopname: TextView = ll.findViewById(R.id.name) as TextView
                val shopicon: ImageView = ll.findViewById(R.id.shoppic) as ImageView
                try {
                    val url:String="https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+array.getJSONObject(i).getJSONArray("photos").getJSONObject(0).getString("photo_reference")+"&key="+getString(R.string.google_maps_key);
Picasso.get().load(url).placeholder(R.drawable.store_icon).into(shopicon)
                }catch (e:Exception){
                    e.printStackTrace()
                }
                shopname.text=array.getJSONObject(i).getString("name")
                    latti=array.getJSONObject(i).getJSONObject("geometry")
                        .getJSONObject("location").getDouble("lat");
                    longi=array.getJSONObject(i).getJSONObject("geometry")
                        .getJSONObject("location").getDouble("lng");
                ll.tag=GeoPoint(array.getJSONObject(i).getJSONObject("geometry")
                    .getJSONObject("location").getDouble("lat"),array.getJSONObject(i).getJSONObject("geometry")
                    .getJSONObject("location").getDouble("lng"))
                ll.setOnClickListener(View.OnClickListener {
                    val bundle = Bundle()
                    bundle.putDouble("latti",array.getJSONObject(i).getJSONObject("geometry")
                        .getJSONObject("location").getDouble("lat"))
                    bundle.putDouble("longi",array.getJSONObject(i).getJSONObject("geometry")
                        .getJSONObject("location").getDouble("lng"))
                    bundle.putString("adress",array.getJSONObject(i).getString("formatted_address"))
                    view?.findNavController()?.navigate(R.id.busimap,bundle)
                })
                    mMap.clear();
                    mMap.addMarker(MarkerOptions().position(LatLng(latti!!, longi!!)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(latti!!, longi!!)));
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(latti!!, longi!!),
                            15f
                        )
                    );
                    viewModal?.setLocation(latti!!, longi!!)

            }
        }catch (exception: Exception){

        }

    }

    override fun onFailed(message: String) {
        context?.toast(message)
    }

    override fun onMapReady(p0: GoogleMap?) {
        if (p0 != null&& latti !=null&&longi!=null) {
            mMap=p0
            if (isPermissionsGranted()){
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
            }
            val loc = LatLng(latti!!, longi!!)
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
            mMap.setOnMapClickListener { latLng ->
                mMap.clear()
                mMap.addMarker(MarkerOptions().position(latLng))
                viewModal?.setLocation(latLng.latitude, latLng.longitude)

            }
        }
    }
}


