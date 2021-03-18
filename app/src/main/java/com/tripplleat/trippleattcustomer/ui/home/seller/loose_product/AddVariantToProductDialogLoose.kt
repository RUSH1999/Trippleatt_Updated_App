package com.tripplleat.trippleattcustomer.ui.home.seller.loose_product

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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.LayoutDialogAddProductVariantLooseBinding
import com.tripplleat.trippleattcustomer.modal.VariantLoose
import com.tripplleat.trippleattcustomer.modal.VariantSellerLoose
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal
import com.tripplleat.trippleattcustomer.util.showDialog
import com.tripplleat.trippleattcustomer.util.toast
import java.io.ByteArrayOutputStream
/*
* This fragment will add the variant in a loose product , it will work when already a product exist in the
* database and we have add its variant and the variant is loose , then this fragment will come into picture
* */
class AddVariantToProductDialogLoose : Fragment(), AdapterView.OnItemSelectedListener {
    private lateinit var txtCamera : TextView
    private lateinit var txtFile : TextView
    private lateinit var viewModel : HomeViewModal
    private lateinit var btnSubmit : Button
    private lateinit var radioGroup : RadioGroup
    private lateinit var txtKg : TextView
    private lateinit var txtLiter : TextView
    private lateinit var spinner : Spinner
    var bitmap : Bitmap? = null
    var pd : Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : LayoutDialogAddProductVariantLooseBinding = DataBindingUtil.inflate(inflater, R.layout.layout_dialog_add_product_variant_loose,container,false)
         viewModel = ViewModelProvider(requireActivity()).get(HomeViewModal::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        initViwes(binding.root)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.DialogrbPacked){
                view?.findNavController()?.navigate(R.id.action_addVariantToProductDialogLoose_to_addVariantToProductDialog)
            }
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
                view?.findNavController()?.navigate(R.id.action_addVariantToProductDialogLoose_to_homeFragment)
            }
            else if(it == -1){
                pd?.dismiss()
                context?.toast("Some error ocurred")
                viewModel.isVariantUploaded.value = 0
                view?.findNavController()?.navigate(R.id.action_addVariantToProductDialogLoose_to_homeFragment)
            }
        })

       viewModel._variantTypeLoose.observe(viewLifecycleOwner, Observer {
            if(it.equals("kg")){
                txtKg.setBackgroundResource(R.drawable.variant_background)
                txtLiter.setBackgroundResource(R.drawable.stroke_otp)
                viewModel.looseDialogVariantUnit = "kg"
            }
            if(it.equals("liter")){
                txtLiter.setBackgroundResource(R.drawable.variant_background)
                txtKg.setBackgroundResource(R.drawable.stroke_otp)
                viewModel.looseDialogVariantUnit = "Liter"
            }
        })

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
            if(viewModel.productName.isNullOrEmpty() || viewModel.looseDialogSellingPrice.isNullOrEmpty() || viewModel.looseDialogStock.isNullOrEmpty()){
                context?.toast("please fill all the details in feild")
            }
            else if(viewModel.looseDialogImage == null){
                context?.toast("please select the image")
            }
            else{
                pd = context?.showDialog("please wait")
                pd?.show()
                val local = VariantLoose(viewModel.category!!,"will be added","will be added",viewModel.productName!!,viewModel.looseDialogVariantUnit)
                val seller = VariantSellerLoose(true,viewModel.looseDialogSellingPrice!!,viewModel.looseDialogStock!!,viewModel.looseDialogVariantUnit,"will be added")
                viewModel.addVariantToProductLoose(local,seller)
            }
        }

        return binding.root
    }

    private fun initViwes(root: View) {

        txtCamera = root.findViewById(R.id.looseClickImageDialog)
        txtFile = root.findViewById(R.id.looseSelectFileImageDialog)
        btnSubmit = root.findViewById(R.id.btnSubmitForPublishLooseDialog)
        radioGroup = root.findViewById(R.id.radioGroupLooseDialog)
        txtKg = root.findViewById(R.id.txtAddProductKgDialog)
        txtLiter = root.findViewById(R.id.txtAddProductLiterDialog)
        spinner = root.findViewById(R.id.spinProductListLoose)
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
                viewModel.looseDialogImage = selectedPhotoUri
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
                    viewModel.looseDialogImage = Uri.parse(path.toString())
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