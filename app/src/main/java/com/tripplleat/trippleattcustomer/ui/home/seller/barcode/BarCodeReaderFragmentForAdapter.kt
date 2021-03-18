package com.tripplleat.trippleattcustomer.ui.home.seller.barcode

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal
import com.tripplleat.trippleattcustomer.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val camera_request_code = 102

/*
* This is a barcode reader which read the barcode from the adapter and fill the data
* according to the form present in the adapter
* */

class BarCodeReaderFragmentForAdapter() : Fragment() {

    private  lateinit var barCodeScanner : CodeScanner
    private lateinit var scannerView: CodeScannerView
    private  var index : Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_barcode_for_adapter, container, false)
        val viewModel = ViewModelProvider(requireActivity()).get(HomeViewModal::class.java)
        Log.i("check","$index")
        scannerView = view.findViewById<CodeScannerView>(R.id.barcodeScannerView)

        setUpPermission()
        codeScanner(viewModel)


        return view
    }

    fun codeScanner(viewModel : HomeViewModal){
        barCodeScanner = CodeScanner(requireContext(),scannerView)
        barCodeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                lifecycleScope.launch(Dispatchers.Main) {
                    if(viewModel.packed){
                        viewModel.variantList_Local[index!!].barcode = it.toString()
                        delay(500L)
                        view?.findNavController()?.navigate(R.id.action_barCodeReaderFragmentForAdapter_to_addProductFragment)
                    }
                    else{
                       // viewModel.variantListLoose[index!!].barcode = it.toString()
                        delay(500L)
                        view?.findNavController()?.navigate(R.id.action_barCodeReaderFragmentForAdapter_to_addProductLooseFragment)
                    }


                }
            }

            errorCallback = ErrorCallback {
                lifecycleScope.launch(Dispatchers.Main) {
                    Log.i("errror","Camera intitlization error ${it.message}")
                }
            }
        }

        scannerView.setOnClickListener {
            barCodeScanner.startPreview()
        }


    }

    override fun onResume() {
        super.onResume()
        barCodeScanner.startPreview()
    }

    override fun onPause() {
        barCodeScanner.releaseResources()
        super.onPause()
    }

    private fun setUpPermission(){
        val permission = ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CAMERA)

        if(permission != PackageManager.PERMISSION_GRANTED)
            makeRequest()
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA),
            camera_request_code
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            camera_request_code -> {
                if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    context?.toast("Camera is important for the Scanning")
                }
                else{
                    //success
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args =
            BarCodeReaderFragmentForAdapterArgs.fromBundle(
                requireArguments()
            )
        index = args.index
    }

}