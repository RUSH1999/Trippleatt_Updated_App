package com.tripplleat.trippleattcustomer.ui.home.seller.barcode

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.budiyev.android.codescanner.*

import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal
import com.tripplleat.trippleattcustomer.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/*
* This is a barcode reader fragment which read the barcode from the fragments and feed it into the form
* */

private const val camera_request_code = 102

class BarcodeReaderFragment : Fragment() {

    private lateinit var barCodeScanner: CodeScanner
    private lateinit var scannerView: CodeScannerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_barcode_reader, container, false)
        val viewModel = ViewModelProvider(requireActivity()).get(HomeViewModal::class.java)

        scannerView = view.findViewById<CodeScannerView>(R.id.barcodeScannerView)

        setUpPermission()
        codeScanner(viewModel)


        return view
    }

    fun codeScanner(viewModal: HomeViewModal) {
        barCodeScanner = CodeScanner(requireContext(), scannerView)
        barCodeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false

            decodeCallback = DecodeCallback {
                lifecycleScope.launch(Dispatchers.Main) {
                    viewModal.barcode = it.toString()
                    delay(1000L)
                    if (viewModal.modeOfBarcode == 1)
                        view?.findNavController()
                            ?.navigate(R.id.action_barcodeReaderFragment_to_addProductFragment)
                    else if (viewModal.modeOfBarcode == 2)
                        view?.findNavController()
                            ?.navigate(R.id.action_barcodeReaderFragment_to_addProductLooseFragment)
                    else if (viewModal.modeOfBarcode == 0) {
                        val result = viewModal.findProductWithBarcode(it.toString())
                        if (result != null) {
                            Log.i("product", "Product Found")
                        } else {
                            Log.i("product", "Not Found")
                            context?.toast("product Not Found please add this product")
                            view?.findNavController()
                                ?.navigate(R.id.action_barcodeReaderFragment_to_addProductFragment)
                        }

                    } else if (viewModal.modeOfBarcode == 3) {
                        view?.findNavController()
                            ?.navigate(R.id.action_barcodeReaderFragment_to_addVariantToProductDialog)
                    }
                }
            }

            errorCallback = ErrorCallback {
                lifecycleScope.launch(Dispatchers.Main) {
                    Log.i("errror", "Camera intitlization error ${it.message}")
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

    private fun setUpPermission() {
        val permission =
            ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED)
            makeRequest()
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(android.Manifest.permission.CAMERA),
            camera_request_code
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            camera_request_code -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    context?.toast("Camera is important for the Scanning")
                } else {
                    //success
                }
            }
        }
    }

}
