package com.tripplleat.trippleattcustomer.ui.home.seller

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController

import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.FragmentInitialAddProductfragmentBinding
import com.tripplleat.trippleattcustomer.modal.Variant
import com.tripplleat.trippleattcustomer.modal.VariantLoose
import com.tripplleat.trippleattcustomer.ui.home.seller.loose_product.AddProductDialogloose
import com.tripplleat.trippleattcustomer.ui.home.seller.packed_product.AddProductDialog
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal
import com.tripplleat.trippleattcustomer.util.showDialog
import com.tripplleat.trippleattcustomer.util.toast

/*
* This is a frgamnet which will be opened when user click on add button in home fragment
* then this fragment will take the barcode or the product name and show the droop down list to select the
* perfect variant*/

class InitialAddProductfragment : Fragment() {

    lateinit var actProductName : AutoCompleteTextView
    lateinit var btnAddProduct : Button
    lateinit var viewModel : HomeViewModal
    lateinit var txtBarcode : TextView
    lateinit var addProductDialog: AddProductDialog
    lateinit var addProductDialogLoose : AddProductDialogloose
    var pd : Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentInitialAddProductfragmentBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_initial_add_productfragment,container,false)
         viewModel =  ViewModelProvider(requireActivity()).get(HomeViewModal::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initView(binding.root)

        viewModel.isVariantUploaded.observe(viewLifecycleOwner, Observer {
            if(it == 1){
                context?.toast("VariantUploaded")
                pd?.dismiss()
                viewModel.isVariantUploaded.value = 0
            }
            if (it == -1){
                context?.toast("VariantUploaded")
                pd?.dismiss()
                viewModel.isVariantUploaded.value = 0
            }
            if(it == 2){
                context?.toast("Variant already existed in your account")
                pd?.dismiss()
                viewModel.isVariantUploaded.value = 0
            }
        })

        viewModel.dialogIsclicked.observe(viewLifecycleOwner, Observer {
            if(it == true){
                pd = context?.showDialog("please wait")
                pd?.show()
                viewModel.dialogIsclicked.value = false
            }
        })

        val callBack : OnBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                view?.findNavController()?.navigate(R.id.action_initialAddProductfragment_to_homeFragment)
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callBack)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btnAddProduct.visibility = View.INVISIBLE

        viewModel._actvariantList.observe(viewLifecycleOwner, Observer {variant ->
            actProductName.setAdapter(ArrayAdapter(requireContext(),R.layout.my_act_layout,R.id.txtAct,variant))
        })

        actProductName.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                btnAddProduct.visibility = View.VISIBLE
            }

        })

        actProductName.setOnItemClickListener { parent, view, position, id ->
            val temp = parent.getItemAtPosition(position).toString()
            val stringArr = temp.split("-")
            if(stringArr.size == 1){
                val selectedProduct = parent.getItemAtPosition(position)
                viewModel.productName = selectedProduct.toString()
                navigate()

            }
            else{
                if(stringArr[1].equals(" Loose",true)){
                    val selectedProduct = parent.getItemAtPosition(position) as VariantLoose
                    Log.i("productName","selected_${selectedProduct.variantUnit}")
                    viewModel.current_variant_loose = selectedProduct
                    addProductDialogLoose =
                        AddProductDialogloose()
                    addProductDialogLoose.show(childFragmentManager,"Add Product Dialog Loose")
                }
                else{
                    val selectedProduct = parent.getItemAtPosition(position) as Variant
                    Log.i("productName","selected_${selectedProduct.barcode}")
                    viewModel.current_variant = selectedProduct
                    addProductDialog =
                        AddProductDialog()
                    addProductDialog.show(childFragmentManager,"Add Product Dialog")
                }
            }
        }

        btnAddProduct.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_initialAddProductfragment_to_addProductFragment)
        }

        txtBarcode.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_initialAddProductfragment_to_barcodeReaderFragment)
            viewModel.modeOfBarcode = 0
        }
    }

    private fun initView(root: View) {
        actProductName = root.findViewById(R.id.actVariant)
        btnAddProduct = root.findViewById(R.id.btnAddProduct)
        txtBarcode = root.findViewById(R.id.txtBarcodeIntial)
    }

    private fun navigate(){
        view?.findNavController()?.navigate(R.id.action_initialAddProductfragment_to_addVariantToProductDialog)
    }

}
