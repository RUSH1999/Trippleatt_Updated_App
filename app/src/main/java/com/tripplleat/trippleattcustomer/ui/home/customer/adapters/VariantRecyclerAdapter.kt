package com.tripplleat.trippleattcustomer.ui.home.customer.adapters

import android.app.Dialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.modal.Variant
import com.tripplleat.trippleattcustomer.modal.VariantLoose
import com.tripplleat.trippleattcustomer.modal.VariantSeller
import com.tripplleat.trippleattcustomer.modal.VariantSellerLoose
import com.tripplleat.trippleattcustomer.ui.home.customer.modal.ProductDetails
import com.tripplleat.trippleattcustomer.ui.home.customer.viewmodel.CustomerViewModel
import com.tripplleat.trippleattcustomer.util.showDialog

/*
* This is adapter which actually show the variant of a product inside the product recyclerview
* this is an nested recycler view
* */

class VariantRecyclerAdapter(
    val context: Context,
    val listOfSeller: ArrayList<VariantSeller>?,
    val looseListOfSeller: ArrayList<VariantSellerLoose>?,
    val mapOfpacked: HashMap<String, Variant>,
    val mapOfLoose: HashMap<String, VariantLoose>,
    val imageView: ImageView,
    val txtView: TextView,
    var currentProduct: HashMap<Int,ProductDetails>,
    var pos : Int,
    val viewMOdel: CustomerViewModel
) : RecyclerView.Adapter<VariantRecyclerAdapter.ViewHolder>() {
    var pd : Dialog? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_variant_recyclerview_customer, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        var size = 0
        if (!listOfSeller.isNullOrEmpty())
            size += listOfSeller.size
        if (!looseListOfSeller.isNullOrEmpty())
            size += looseListOfSeller.size
        return size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (!listOfSeller.isNullOrEmpty() && position < listOfSeller.size) {
            val variant = listOfSeller[position]
            val temp = mapOfpacked.get(variant.vairantId)
            holder.txtVariantName.text = temp!!.variant
            if (position == 0) {
                Picasso.get().load(temp.image).error(R.drawable.home).into(imageView)
                txtView.text = variant.sellingPrice
            }
        } else {
            if (!looseListOfSeller.isNullOrEmpty()) {
                val variant = looseListOfSeller[position]
                val temp = mapOfLoose.get(variant.vairantId)
                holder.txtVariantName.text = "Loose"
                if (position == 0) {
                    Picasso.get().load(temp!!.image).error(R.drawable.home).into(imageView)
                    txtView.text = variant.sellingPrice + " / ${temp.variantUnit}"
                }
            }
        }

        holder.txtVariantName.setOnClickListener {
            if (!listOfSeller.isNullOrEmpty() && position < listOfSeller.size) {

                val variant = listOfSeller[position]
                val temp = mapOfpacked.get(variant.vairantId)
                Picasso.get().load(temp?.image).error(R.drawable.home).into(imageView)
                txtView.text = variant.sellingPrice
                it.setBackgroundResource(R.drawable.variant_background)
                val product = ProductDetails(temp!!.productName,temp.variant,temp.image,temp.mrp.toInt(),variant.sellingPrice.toInt(),0,variant.Stock.toInt(),"none",0,"added Later","added Later","added Later","addedLater",variant.vairantId)
                currentProduct.put(pos,product)
                Log.i("productDetails","$currentProduct")


            } else {
                if (!looseListOfSeller.isNullOrEmpty()) {
                    val variant = looseListOfSeller[position]
                    val temp = mapOfLoose.get(variant.vairantId)
                    Picasso.get().load(temp?.image).error(R.drawable.home).into(imageView)
                    txtView.text = variant.sellingPrice + " / ${temp!!.variantUnit}"
                    it.setBackgroundResource(R.drawable.variant_background)
                    val product = ProductDetails(temp!!.productName,"Loose",temp.image,0,variant.sellingPrice.toInt(),0,variant.Stock.toInt(),temp.variantUnit,0,"added Later","added Later","added later","addedLater",variant.vairantId)
                    currentProduct.put(pos,product)
                    Log.i("productDetails","$currentProduct")

                }
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtVariantName: TextView = view.findViewById(R.id.txtVariantnameCustomer)
    }
}