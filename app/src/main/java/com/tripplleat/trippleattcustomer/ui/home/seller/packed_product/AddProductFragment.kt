package com.tripplleat.trippleattcustomer.ui.home.seller.packed_product

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.FragmentAddProductBinding
import com.tripplleat.trippleattcustomer.ui.home.seller.HomeViewModelFactory
import com.tripplleat.trippleattcustomer.ui.home.seller.adapter.PackedProductRecyclerViewAdapter
import com.tripplleat.trippleattcustomer.ui.home.seller.listeners.ActivityForResultListener
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal
import com.tripplleat.trippleattcustomer.util.showDialog
import com.tripplleat.trippleattcustomer.util.toast
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.ByteArrayOutputStream

/*
* This is a fragment used to add Packed product in the seller list , this work when we have to add
* a new product in the database which does not exist in the database
* */

class AddProductFragment : Fragment(),AdapterView.OnItemSelectedListener,ActivityForResultListener,
    KodeinAware {

    lateinit var productList : ArrayList<String>
    lateinit var spinner : Spinner
    lateinit var radioGroup : RadioGroup
    lateinit var recyclerviewAddProduct :  RecyclerView
    lateinit var viewModel : HomeViewModal
    lateinit var layoutManager : LinearLayoutManager
    lateinit var packedProductAdapter : PackedProductRecyclerViewAdapter
    var bitmap = MutableLiveData<Bitmap>()
    var imgUri : Uri? = null
    var pd : Dialog? = null

    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: HomeViewModelFactory by instance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentAddProductBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_add_product,container,false)
         viewModel = ViewModelProvider(requireActivity(),factory).get(HomeViewModal::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        viewModel.activityresultListener = this
        initViews(binding.root)

        layoutManager = LinearLayoutManager(activity as Context)
        packedProductAdapter = PackedProductRecyclerViewAdapter(viewModel.allVaraints,viewModel.variantList_Local,viewModel.variantList_seller, viewModel.imageList)
        recyclerviewAddProduct.layoutManager = layoutManager
        recyclerviewAddProduct.adapter = packedProductAdapter
        packedProductAdapter.notifyDataSetChanged()

        packedProductAdapter.activityResultListenerAdapter = this

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.rbLoose){
                viewModel.packed = false
                view?.findNavController()?.navigate(R.id.action_addProductFragment_to_addProductLooseFragment)
            }

            }

        val variantLiveData : LiveData<Int> = viewModel.allVaraints
        variantLiveData.observe(viewLifecycleOwner, Observer {
            packedProductAdapter.notifyDataSetChanged()
        })

        val bitLiveData : LiveData<Bitmap> = bitmap
        bitLiveData.observe(viewLifecycleOwner, Observer {
            viewModel.bitmap = bitmap.value
            if(viewModel.imageList.size == viewModel.allVaraints.value)
                viewModel.imageList.add(imgUri!!)
            else{
                viewModel.imageList.set(viewModel.allVaraints.value!!,imgUri!!)
                Log.i("check_camera","selected ${bitmap.value}")
            }
            Log.i("check","image ${viewModel.imageList.size}")
            Log.i("bitmap",viewModel.bitmap.toString())
        })

        val barcode: LiveData<Boolean> = viewModel.barCodeClicked
        barcode.observe(viewLifecycleOwner, Observer {
            if(it == true){
                view?.findNavController()?.navigate(R.id.action_addProductFragment_to_barcodeReaderFragment)
                viewModel.barCodeClicked.value = false
            }
        })

        val upload : LiveData<Int> = viewModel.isUploaded
        upload.observe(viewLifecycleOwner, Observer {
            if(it == (viewModel.variantList_Local.size - 1)){
                viewModel.isUploaded.value = -2
                pd?.dismiss()
                context?.toast("Upload Successful")
                viewModel.variantList_Local.clear()
                viewModel.variantList_seller.clear()
                viewModel.imageList.clear()
                view?.findNavController()?.navigate(R.id.action_addProductFragment_to_homeFragment)
            }
            else if (it == -1){
                viewModel.isUploaded.value = -2
                pd?.dismiss()
                context?.toast("Please Retry After Some time")
            }
        })

        val submitLivedata : LiveData<Boolean> = viewModel.submitClicked
        submitLivedata.observe(viewLifecycleOwner, Observer {
            if(it == true){
                viewModel.submitClicked.value = false
                pd = context?.showDialog("please wait")
                pd?.show()
            }
        })



        val callBack : OnBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                view?.findNavController()?.navigate(R.id.action_addProductFragment_to_homeFragment)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callBack)
        viewModel._productName.observe(viewLifecycleOwner, Observer {
            Log.i("productname","list${it}")
        })

        return binding.root
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        requireContext().toast("please select valid Category")
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

    private fun initViews(view: View){
        spinner = view.findViewById(R.id.spinProductList)
        radioGroup = view.findViewById(R.id.radioGroup)
        recyclerviewAddProduct = view.findViewById(R.id.packedProductRecyclerView)
        spinner.onItemSelectedListener = this
        if(viewModel.category_position != -1){
            spinner.setSelection(viewModel.category_position)
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
                imgUri = selectedPhotoUri
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
                     imgUri = Uri.parse(path.toString())
                 }
             }
         }
    }



}
