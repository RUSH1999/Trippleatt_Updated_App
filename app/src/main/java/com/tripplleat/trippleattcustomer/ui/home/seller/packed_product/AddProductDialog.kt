package com.tripplleat.trippleattcustomer.ui.home.seller.packed_product

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.ViewModelProvider
import com.squareup.picasso.Picasso
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal
import com.tripplleat.trippleattcustomer.util.toast

/*
* This dialog will be open when the packed product will added to seller , it will bw used when the product already exist in the
* database and only stock and selling price should be entred by the seller
* */

class AddProductDialog : AppCompatDialogFragment() {
    private lateinit var txtProductName : TextView
    private lateinit var txtVariantName : TextView
    private lateinit var txtMrp : TextView
    private lateinit var imgVaraint : ImageView
    private lateinit var etPrice : EditText
    private lateinit var etStock : EditText
    private lateinit var viewModel : HomeViewModal

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view =  inflater.inflate(R.layout.layout_dialog_add_variant,null)

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModal::class.java)

        builder.setView(view)
        builder.setPositiveButton("Publish"){text,listener ->
            if(etPrice.text.isNullOrEmpty() || etStock.text.isNullOrEmpty()){
                context?.toast("please enter the valid details")
            }
            else{
                viewModel.addNewVariantToSeller(etPrice.text.toString(),etStock.text.toString())
            }
        }
        builder.setNegativeButton("Cancel"){text,listener ->

        }



        txtProductName = view.findViewById(R.id.txtProductName)
        txtMrp = view.findViewById(R.id.txtVariantMrp)
        txtVariantName = view.findViewById(R.id.txtVaraintName)
        etPrice = view.findViewById(R.id.etvariantPrice)
        etStock = view.findViewById(R.id.etVariantStock)
        imgVaraint = view.findViewById(R.id.variantImage)

        txtProductName.text = viewModel.current_variant?.productName
        txtVariantName.text = viewModel.current_variant?.variant
        txtMrp.text = viewModel.current_variant?.mrp
        Picasso.get().load(viewModel.current_variant?.image).error(R.drawable.ic_launcher_background).into(imgVaraint)

        return builder.create()
    }
}