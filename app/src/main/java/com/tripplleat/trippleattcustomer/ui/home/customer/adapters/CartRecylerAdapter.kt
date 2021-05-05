package com.tripplleat.trippleattcustomer.ui.home.customer.adapters

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.ui.home.customer.modal.ProductDetails
import com.tripplleat.trippleattcustomer.ui.home.customer.viewmodel.CustomerViewModel
import com.tripplleat.trippleattcustomer.util.toast

/*
* This adapter will work when the user will click on the cart image and
* then we have to show the all products present inside the cart and we have some functionality
* like removal of product
* */

class CartRecylerAdapter(
    val context: Context,
    val cartList: ArrayList<ProductDetails>,
    val viewModel: CustomerViewModel,
    val bottomSheet: BottomSheetBehavior<LinearLayout>
) : RecyclerView.Adapter<CartRecylerAdapter.ViewHolder>() {

    var pos: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cart_products_design, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = cartList[position]
        holder.txtProductName.text = product.productName
        holder.txtQuantity.text = "Qty : ${product.orderedQuantity}"
        holder.txtVariantName.text = product.variantName
        holder.txtprice.text = product.totalPrice.toString()
        Picasso.get().load(product.image).error(R.drawable.home).into(holder.imgProduct)
        holder.txtRemove.setOnClickListener {
            viewModel.removeProductFormCart(product)
        }
        holder.txtQuantity.setOnClickListener {
            if (bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                pos = position
            } else
                bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED

        }

        /*
        * This is a bottom sheet callback which will work when the state of bottom sheet change,
        * that is when the user click on the quantity because that is the only field which can be changed by the user and
        * the lsitener work on this only.
        * */
        bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    val product = cartList[pos]
                    val txtProductName: TextView =
                        bottomSheet.findViewById(R.id.txtProductNameBottomCustomer)
                    val txtVariantName: TextView =
                        bottomSheet.findViewById(R.id.txtVariantNameBottomCustomer)
                    val txtProductPrice: TextView =
                        bottomSheet.findViewById(R.id.txtProductPriceBottomCustomer)
                    val image: ImageView = bottomSheet.findViewById(R.id.imgProductBottomCustomer)
                    val btn: Button = bottomSheet.findViewById(R.id.btnAddProductBottomCustomer)
                    val bn:Button=bottomSheet.findViewById(R.id.btnBuyNowBottomCustomer)
                    val etQuantity: EditText =
                        bottomSheet.findViewById(R.id.etProductQuantityBottomCustomer)
                    val txtTotalPrice: TextView =
                        bottomSheet.findViewById(R.id.txtTotalpriceBottomcustomer)
                    //val txtBuyNow:TextView=bottomSheet.findViewById(R.id.txtBuyNow)

                    txtProductName.text = product.productName
                    txtVariantName.text = product.variantName
                    etQuantity.setText(product.orderedQuantity.toString())
                    if (product.variantName.equals("Loose")) {
                        txtProductPrice.text = "${product.sellingPrice} / ${product.variantUnit}"
                        txtTotalPrice.text = "${product.totalPrice}"
                    } else {
                        txtProductPrice.text = "${product.sellingPrice}"
                        txtTotalPrice.text = "${product.totalPrice}"
                    }
                    Picasso.get().load(product.image).error(R.drawable.home).into(image)

                    etQuantity.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {
                            if (!s.isNullOrEmpty()) {
                                val sp = product.sellingPrice
                                val newTotal = sp * (s.toString().toInt())
                                txtTotalPrice.text = newTotal.toString()
                            }
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {

                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                        }

                    })

                    btn.setOnClickListener {
                        if (!etQuantity.text.isNullOrEmpty() && etQuantity.text.toString()
                                .toInt() != 0
                        ) {
                            product.orderedQuantity = etQuantity.text.toString().toInt()
                            product.totalPrice = txtTotalPrice.text.toString().toInt()
                            viewModel.updateProdutcInCart(product)
                            //productAdded()
                        } else {
                            context.toast("please enter valid quantity then add to cart")
                        }
                    }

                    bn.setOnClickListener{
                        if (!etQuantity.text.isNullOrEmpty() && etQuantity.text.toString()
                                .toInt() != 0 && etQuantity.text.toString()
                                .toInt() <= product!!.existedQunatity
                        ) {
                            var flag = 0
                            product.orderedQuantity = etQuantity.text.toString().toInt()
                            product.totalPrice = txtTotalPrice.text.toString().toInt()
                            if (!product.variantName.equals("none")) {
                                for (i in 0 until (cartList?.size ?: 0)) {
                                    val pDetails = cartList?.get(i)
                                    if (pDetails!!.productName.equals(product.productName) && pDetails.variantName.equals(
                                            product.variantName
                                        )
                                    ) {
                                        flag = -1
                                    }
                                    else if(product.orderedQuantity > product.existedQunatity){
                                        flag = -2
                                    }
                                }
                            } else {
                                for (i in 0 until (cartList?.size ?: 0)) {
                                    val pDetails = cartList?.get(i)
                                    if (pDetails!!.productName.equals(product.productName)) {
                                        flag = -1
                                    }
                                    else if(product.orderedQuantity > product.existedQunatity){
                                        flag = -2
                                    }
                                }
                            }

                            if (flag == 0) {
                                cartList?.add(product)
                                Log.i("cart_product", "$cartList")
                                //viewModel.addProductTocart(product)
                                viewModel.addProductTomyOrders(product)
                                productAdded()
                                etQuantity.setText("1")
                            } else if(flag == -1) {
                                cartList?.add(product)
                                viewModel.addProductTomyOrders(product)

                                //context.toast("This variant already exist in your cart")
                                productAdded()
                                etQuantity.setText("1")
                            }
                            else if(flag == -2){
                                context.toast("Exceeding the max quantity")
                                product.orderedQuantity = product.existedQunatity
                            }
                        } else {
                            context.toast("Exceeding the max quantity you can buy only ${product.existedQunatity} products")
                            etQuantity.setText(product.existedQunatity.toString())
                        }
                    }

                }
            }

        })
    }
    //This is a funnction to collapse the bootm sheet which is expanded
     fun productAdded() {
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        // context.toast("product added sucessfully to your cart")
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.imgProductCart)
        val txtRating: TextView = view.findViewById(R.id.txtProductRatingCart)
        val txtProductName: TextView = view.findViewById(R.id.txtProductNameCart)
        val txtVariantName: TextView = view.findViewById(R.id.txtVariantNameCart)
        val txtDescription: TextView = view.findViewById(R.id.txtDescriptionCart)
        val txtprice: TextView = view.findViewById(R.id.txtProductPriceCart)
        val txtQuantity: TextView = view.findViewById(R.id.txtProductQuantityCart)
        val txtSave: TextView = view.findViewById(R.id.txtSaveForLater)
        val txtRemove: TextView = view.findViewById(R.id.txtProductRemoveCart)
        //val txtBuyNow:TextView=view.findViewById(R.id.txtBuyNow)
    }
}