package com.tripplleat.trippleattcustomer.ui.home.seller.packed_product

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.LayoutDialogAddProductVariantBinding
import com.tripplleat.trippleattcustomer.modal.Variant
import com.tripplleat.trippleattcustomer.modal.VariantSeller
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal
import com.tripplleat.trippleattcustomer.util.showDialog
import com.tripplleat.trippleattcustomer.util.toast
import java.io.ByteArrayOutputStream

/*
* This fragment will add the variant in a packed product , it will work when already a product exist in the
* database and we have add its variant and the variant is packed , then this fragment will come into picture
* */

class AddVariantToProductDialog : Fragment(),AdapterView.OnItemSelectedListener {
    private lateinit var spinner : Spinner
   private lateinit var txtBarcode : TextView
   private lateinit var txtCamera : TextView
   private lateinit var txtFile : TextView
    private lateinit var viewModel : HomeViewModal
    private lateinit var btnSubmit : Button
    private lateinit var radioGroup : RadioGroup
    var pd : Dialog? = null
    var bitmap : Bitmap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding : LayoutDialogAddProductVariantBinding = DataBindingUtil.inflate(inflater,R.layout.layout_dialog_add_product_variant,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModal::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this

        initViews(binding.root)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            view?.findNavController()?.navigate(R.id.action_addVariantToProductDialog_to_addVariantToProductDialogLoose)
        }

        viewModel.isVariantUploaded.observe(viewLifecycleOwner, Observer {
            if(it == 2){
                pd?.dismiss()
                context?.toast("The Variant already Existed")
                viewModel.isVariantUploaded.value = 0
            }
            if(it == 1){
                pd?.dismiss()
                context?.toast("Variant Uploaded sucessfully")
                viewModel.isVariantUploaded.value = 0
                view?.findNavController()?.navigate(R.id.action_addVariantToProductDialog_to_homeFragment)
            }
            else if(it == -1){
                pd?.dismiss()
                context?.toast("Some error ocurred")
                viewModel.isVariantUploaded.value = 0
                view?.findNavController()?.navigate(R.id.action_addVariantToProductDialog_to_homeFragment)
            }
        })

        txtBarcode.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_addVariantToProductDialog_to_barcodeReaderFragment)
            viewModel.modeOfBarcode = 3
        }

        txtFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            startActivityForResult(intent,100)
        }

        txtCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,101)
        }

        btnSubmit.setOnClickListener {
            if (viewModel.productName.isNullOrEmpty() || viewModel.barcode.isNullOrEmpty() || viewModel.packedDialogVariant.isNullOrEmpty() || viewModel.packedDialogStock.isNullOrEmpty() || viewModel.packedDialogSellingPrice.isNullOrEmpty() || viewModel.packedDialogMrp.isNullOrEmpty()) {
                context?.toast("please fill all the details in feild")
            } else if (viewModel.packedDialogImage == null) {
                context?.toast("please select the product image")
            } else {
                val local = Variant(
                    viewModel.barcode!!,
                    viewModel.category!!,
                    "will be added",
                    "image added",
                    viewModel.packedDialogMrp!!,
                    viewModel.productName!!,
                    viewModel.packedDialogVariant!!
                )
                val seller = VariantSeller(true,
                    viewModel.packedDialogSellingPrice!!,
                    viewModel.packedDialogStock!!,
                    "added later"
                )
                pd = context?.showDialog("please wait")
                pd?.show()
                viewModel.addVariantToProduct(local,seller)
            }
        }

        val callBack : OnBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                view?.findNavController()?.navigate(R.id.action_addVariantToProductDialog_to_initialAddProductfragment)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callBack)


        return binding.root
    }



    private fun initViews(root: View) {
        spinner = root.findViewById(R.id.spinProductList)
        txtBarcode = root.findViewById(R.id.txtDialogBarCodeReader)
        txtCamera = root.findViewById(R.id.txtDialogClickImageOfProduct)
        txtFile = root.findViewById(R.id.txtDialogUploadImageOfProduct)
        btnSubmit = root.findViewById(R.id.btnDialogSubmitForPublish)
        radioGroup = root.findViewById(R.id.radioGroupDialog)
        spinner.onItemSelectedListener = this
        if(viewModel.category_position != -1){
            spinner.setSelection(viewModel.category_position)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            if(resultCode == Activity.RESULT_OK){
                val selectedPhotoUri = data?.data
                viewModel.packedDialogImage = selectedPhotoUri
                try {
                    selectedPhotoUri?.let {
                        if(Build.VERSION.SDK_INT < 28) {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                selectedPhotoUri
                            )
                        } else {
                            val source = ImageDecoder.createSource(requireContext().contentResolver, selectedPhotoUri)
                            bitmap = ImageDecoder.decodeBitmap(source)

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        if (requestCode == 101){
            if(resultCode == Activity.RESULT_OK){
                if(ContextCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(requireActivity(), arrayOf((android.Manifest.permission.CAMERA)),101)
                }
                if (data != null) {

                    bitmap = data.extras?.get("data") as Bitmap
                    val bytes = ByteArrayOutputStream()
                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path = MediaStore.Images.Media.insertImage(context?.contentResolver, bitmap, "Title", null)
                    viewModel.packedDialogImage = Uri.parse(path.toString())
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if(position == 0){
            requireContext().toast("please select valid Category")
            viewModel.category = null
        }
        else{
            viewModel.category = spinner.selectedItem.toString()
            viewModel.category_position = position
        }
    }


}