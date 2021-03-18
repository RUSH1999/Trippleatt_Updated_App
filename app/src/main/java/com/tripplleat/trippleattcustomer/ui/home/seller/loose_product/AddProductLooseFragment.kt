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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.FragmentAddProductLooseBinding
import com.tripplleat.trippleattcustomer.ui.home.seller.listeners.ActivityForResultListener
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal
import com.tripplleat.trippleattcustomer.util.showDialog
import com.tripplleat.trippleattcustomer.util.toast
import java.io.ByteArrayOutputStream

/*
* This is a fragment used to add loose product in the seller list , this work when we have to add
* a new product in the database which does not exist in the database
* */

class AddProductLooseFragment : Fragment(), AdapterView.OnItemSelectedListener, ActivityForResultListener {

    lateinit var spinner : Spinner
    lateinit var radioGroup : RadioGroup
    lateinit var txtKg : TextView
    lateinit var txtLiter : TextView
    lateinit var viewModel : HomeViewModal
    var category : Boolean = false
    var bitmap = MutableLiveData<Bitmap>()
    var imgUri : Uri? = null
    var pd : Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding  : FragmentAddProductLooseBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_product_loose,container,false)
        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModal::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        viewModel.activityresultListenerLoose = this
        initViews(binding.root)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.rbPacked){
                viewModel.packed = true
                view?.findNavController()?.navigate(R.id.action_addProductLooseFragment_to_addProductFragment)
            }

        }

        val variantData : LiveData<String> = viewModel._variantTypeLoose
        variantData.observe(viewLifecycleOwner, Observer {
            if(it.equals("kg")){
                txtKg.setBackgroundResource(R.drawable.variant_background)
                txtLiter.setBackgroundResource(R.drawable.stroke_otp)
                viewModel.variantUnit = "Kg"
            }
            if(it.equals("liter")){
                txtLiter.setBackgroundResource(R.drawable.variant_background)
                txtKg.setBackgroundResource(R.drawable.stroke_otp)
                viewModel.variantUnit = "Liter"
            }
        })

        val upload : LiveData<Int> = viewModel.isUploaded
        upload.observe(viewLifecycleOwner, Observer {
            if(it == 1){
                viewModel.isUploaded.value = 0
                pd?.dismiss()
                context?.toast("Uploaded Sucessfully")
                viewModel.submitClicked.value = false
                view?.findNavController()?.navigate(R.id.homeFragment)
            }
            else if(it == 2){
                viewModel.isUploaded.value = 0
                pd?.dismiss()
                context?.toast("Retry after Some Time")
                viewModel.submitClicked.value = false
                view?.findNavController()?.navigate(R.id.homeFragment)
            }
            else if(it == 3){
                viewModel.isUploaded.value = 0
                pd?.dismiss()
                context?.toast("Product already present")
                viewModel.submitClicked.value = false
                view?.findNavController()?.navigate(R.id.homeFragment)
            }
        })

        viewModel.submitClicked.observe(viewLifecycleOwner, Observer {
            if(it == true){
                pd = context?.showDialog("please wait")
                pd?.show()
            }
        })

        return binding.root
        }

    private fun initViews(view: View){
        spinner = view.findViewById(R.id.spinProductList)
        radioGroup = view.findViewById(R.id.radioGroup)
        txtKg = view.findViewById(R.id.txtAddProductKg)
        txtLiter = view.findViewById(R.id.txtAddProductLiter)
        spinner.onItemSelectedListener = this
        if(viewModel.category_position != -1){
            spinner.setSelection(viewModel.category_position)
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

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

    override fun StartActivityForResult(intent: Intent?, requestCode: Int) {
        startActivityForResult(intent,requestCode)
    }
    /*
    * This function works when the user click on camera or
    * click on select file to update/select the image of the product
    * */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100){
            if(resultCode == Activity.RESULT_OK){
                val selectedPhotoUri = data?.data
                viewModel.Image = selectedPhotoUri
                try {
                    selectedPhotoUri?.let {
                        if(Build.VERSION.SDK_INT < 28) {
                            bitmap.value = MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                selectedPhotoUri
                            )
                        } else {
                            val source = ImageDecoder.createSource(requireContext().contentResolver, selectedPhotoUri)
                            bitmap.value = ImageDecoder.decodeBitmap(source)

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
                    bitmap.value = data.extras?.get("data") as Bitmap
                    val bytes = ByteArrayOutputStream()
                    bitmap.value!!.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path = MediaStore.Images.Media.insertImage(context?.contentResolver, bitmap.value, "Title", null)
                    viewModel.Image = Uri.parse(path.toString())
                }
            }
        }
    }

}

