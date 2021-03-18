package com.tripplleat.trippleattcustomer.ui.home.seller.loose_product

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
* Dialog to add the loose product in the seller database, this will work
* when a product variant already exist in the database and only the stock and price should be entered by the seller
* */

class AddProductDialogloose : AppCompatDialogFragment() {

    private lateinit var txtProductName : TextView
    private lateinit var etUnit : EditText
    private lateinit var imgVaraint : ImageView
    private lateinit var etPrice : EditText
    private lateinit var etStock : EditText
    private lateinit var viewModel : HomeViewModal

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.layout_dialog_add_variant_loose,null)

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModal::class.java)
        builder.setView(view)
        builder.setPositiveButton("Publish"){text,listener ->
            if(etPrice.text.isNullOrEmpty() || etStock.text.isNullOrEmpty() || etUnit.text.isNullOrEmpty()){
                context?.toast("please enter the valid details")
            }
            else{
                viewModel.addNewVariantLooseToSeller(etPrice.text.toString(),etStock.text.toString(),etUnit.text.toString())
            }
        }
        builder.setNegativeButton("Cancel"){text,listener ->

        }


        txtProductName = view.findViewById(R.id.txtProductNameLoose)
        imgVaraint = view.findViewById(R.id.variantImageLoose)
        etUnit = view.findViewById(R.id.etvariantUnitLoose)
        etPrice = view.findViewById(R.id.etVariantSellingPrice)
        etStock = view.findViewById(R.id.etVariantStock)

        txtProductName.text = viewModel.current_variant_loose!!.productName
        Picasso.get().load(viewModel.current_variant_loose!!.image).error(R.drawable.ic_launcher_background).into(imgVaraint)

        return builder.create()
    }
}