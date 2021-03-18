package com.tripplleat.trippleattcustomer.ui.home.seller.adapter

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.modal.Variant
import com.tripplleat.trippleattcustomer.modal.VariantSeller
import com.tripplleat.trippleattcustomer.ui.home.seller.listeners.ActivityForResultListener
import com.tripplleat.trippleattcustomer.ui.home.seller.packed_product.AddProductFragmentDirections
import java.util.ArrayList

/*
* This is an adapter which is used to take the packed product data from the application , because there is packed product and
* where a button exist in form name as addProduct on which when we click a new form get added in the same page and this recycler
* view do this work to show the form
* */

class PackedProductRecyclerViewAdapter(
    var variantSize: MutableLiveData<Int>, //size of the current form is maintain inside this mutable data
    val variantList_Local: ArrayList<Variant>, // it is local variant list which consist the variant present inside the form
    val variantListSeller: ArrayList<VariantSeller>, //The list of vatriant consisting stock and selling price of a varinat
    val imageList: ArrayList<Uri> // list of images of variant
) : RecyclerView.Adapter<PackedProductRecyclerViewAdapter.PackedProductViewHolder>() {

    var activityResultListenerAdapter: ActivityForResultListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackedProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.packed_product_recycler_layout, parent, false)
        return PackedProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return variantSize.value!!
    }

    override fun onBindViewHolder(holder: PackedProductViewHolder, position: Int) {

        holder.setIsRecyclable(false)

        Log.i("check_length", "${position + 1}")

        if (!variantList_Local[position + 1].variant.isEmpty())
            holder.variant.setText(variantList_Local[position + 1].variant)
        else
            holder.variant.setText("")

        if (!variantList_Local[position + 1].barcode.isEmpty())
            holder.barcode.setText(variantList_Local[position + 1].barcode)
        else
            holder.barcode.setText("")

        if (!variantList_Local[position + 1].mrp.isEmpty())
            holder.etMrp.setText(variantList_Local[position + 1].mrp)
        else
            holder.etMrp.setText("")

        if (!variantListSeller[position + 1].Stock.isEmpty())
            holder.etStock.setText(variantListSeller[position + 1].Stock)
        else
            holder.etStock.setText("")

        if (!variantListSeller[position + 1].sellingPrice.isEmpty())
            holder.etSellingPrice.setText(variantListSeller[position + 1].sellingPrice)
        else
            holder.etSellingPrice.setText("")

        /*
        * we are applying the text change listener to capture the current value present in side the edit text and
        * maintain them in a list which further will be added to data base
        * */

        holder.variant.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                variantList_Local[position + 1].variant = s.toString()
                Log.i("check", "${position}")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        holder.barcode.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                variantList_Local[position + 1].barcode = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        holder.etMrp.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                variantList_Local[position + 1].mrp = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        holder.etSellingPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                variantListSeller[position + 1].sellingPrice = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })

        holder.etStock.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                variantListSeller[position + 1].Stock = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
        /*
        * Using camera to capture the product image
        * */
        holder.txtCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            activityResultListenerAdapter?.StartActivityForResult(intent, 101)
        }

        /*
        * saving image file and putting it in the object we needed
        * */

        holder.txtFile.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            activityResultListenerAdapter?.StartActivityForResult(intent, 100)

        }
        /*
        * This is a button to delete the extr variant which we add on click of add variant
        * */
        holder.btnDeleteVaraint.setOnClickListener {
            var temp: Int = variantSize.value!!
            variantList_Local.removeAt(position + 1)
            variantListSeller.removeAt(position + 1)
            if (imageList.size > (position + 1))
                imageList.removeAt(position + 1)
            Log.i("check_size", "${imageList.size}")
            temp--
            variantSize.value = temp
            Log.i("check_variant_size", "${variantList_Local.size}")
        }
        /*
        * this is barcode reader which read barcode from the the barcode and add it to the list of packed product
        * */
        holder.txtBarCodeReader.setOnClickListener {
            val action =
                AddProductFragmentDirections.actionAddProductFragmentToBarCodeReaderFragmentForAdapter(
                    position + 1
                )
            it.findNavController().navigate(action)
        }


    }

    class PackedProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val variant: EditText = view.findViewById(R.id.etVariantName)
        val barcode: EditText = view.findViewById(R.id.etPackedBarCode)
        val txtBarCodeReader: TextView = view.findViewById(R.id.txtBarCodeReaderPacked)
        val etMrp: EditText = view.findViewById(R.id.etPackedMrp)
        val etSellingPrice: EditText = view.findViewById(R.id.etPackedSellingPrice)
        val txtFile: TextView = view.findViewById(R.id.txtUploadImageOfProduct)
        val txtCamera: TextView = view.findViewById(R.id.txtClickImageOfProduct)
        val etStock: EditText = view.findViewById(R.id.etPackedCurrentStock)
        val btnDeleteVaraint: Button = view.findViewById(R.id.btndeleteVariant)
    }

}