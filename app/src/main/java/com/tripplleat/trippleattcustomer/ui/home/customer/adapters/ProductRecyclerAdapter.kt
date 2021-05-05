package com.tripplleat.trippleattcustomer.ui.home.customer.adapters

import android.app.Dialog
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.squareup.picasso.Picasso
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.modal.Variant
import com.tripplleat.trippleattcustomer.modal.VariantLoose
import com.tripplleat.trippleattcustomer.modal.VariantSeller
import com.tripplleat.trippleattcustomer.modal.VariantSellerLoose
import com.tripplleat.trippleattcustomer.ui.home.customer.fragments.ProductsFragment
import com.tripplleat.trippleattcustomer.ui.home.customer.modal.OrderDetails
import com.tripplleat.trippleattcustomer.ui.home.customer.modal.ProductDetails
import com.tripplleat.trippleattcustomer.ui.home.customer.viewmodel.CustomerViewModel
import com.tripplleat.trippleattcustomer.util.toast
import kotlinx.android.synthetic.main.add_product_bottom_sheet.view.*

/*
* This is a recycler adapater which actually recycle the product in the recyclerview,
* this will work when the user click on a shop and then this recycler will show all
* the product present in the current shop.
* */

class ProductRecyclerAdapter(
    var context: Context,
    var productList: List<String>,
    val mapOfProduct: HashMap<String, ArrayList<VariantSeller>>, //This is a map of variantseller which actually contain the stock and price of the product
    val mapOfpackedVariant: HashMap<String, Variant>,//This is a map of variant which actually contain Packed variant details
    val mapOfLooseProduct: HashMap<String, ArrayList<VariantSellerLoose>>,//This is a map of variantseller which actually contain the stock and price of the Loose product
    val mapOfLooseVariant: HashMap<String, VariantLoose>,//This is a map of variant which actually contain loose variant details
    var currentProduct: HashMap<Int, ProductDetails>,//This is a map for current product which is useer seeing right now
    //var currentProduct1: HashMap<Int, OrderDetails>,
    var cartList: ArrayList<ProductDetails>?, // This is a list of product which present inside the cart
    val bottomSheet: BottomSheetBehavior<LinearLayout>,
    val viewModel: CustomerViewModel
) : RecyclerView.Adapter<ProductRecyclerAdapter.ProductViewHolder>() {


    private val viewPool = RecyclerView.RecycledViewPool() // To manage the recycler vire i.e Variant recyclerview inside of ProductRecyclerView
    var pos: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.products_design, parent, false)
        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.txtProductName.text = productList[position]
        val listOfProduct = mapOfProduct.get(productList[position])
        val listOfLooseProduct = mapOfLooseProduct.get(productList[position])
        val childLayoutManager =
            LinearLayoutManager(holder.recyclerview.context, RecyclerView.HORIZONTAL, false)
        if (!listOfProduct.isNullOrEmpty() || !listOfLooseProduct.isNullOrEmpty()) {
            holder.recyclerview.apply {
                layoutManager = childLayoutManager
                adapter = VariantRecyclerAdapter(
                    context,
                    listOfProduct,
                    listOfLooseProduct,
                    mapOfpackedVariant,
                    mapOfLooseVariant,
                    holder.imgProduct,
                    holder.productprice,
                    currentProduct,
                    position,
                    viewModel
                )
                setRecycledViewPool(viewPool)
            }
        }
        fun changeButtonText(str: String) {
            holder.btnAdd.setText(str)
        }

        holder.btnAdd.setOnClickListener {
            if (currentProduct.containsKey(position)) {
                if (bottomSheet.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
                    pos = position

                } else
                    bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                context.toast("Please select a variant of product first")
            }

        }
        /*
        * bottom sheet call back starting from here
        * */
        bottomSheet.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    val product = currentProduct.get(pos)
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

                    txtProductName.text = product!!.productName
                    txtVariantName.text = product.variantName
                    if (product.variantName.equals("Loose")) {
                        txtProductPrice.text = "${product.sellingPrice} / ${product.variantUnit}"
                        txtTotalPrice.text = "${product.sellingPrice}"

                    } else {
                        txtProductPrice.text = "${product.sellingPrice}"
                        txtTotalPrice.text = "${product.sellingPrice}"
                    }
                    Picasso.get().load(product.image).error(R.drawable.home).into(image)

                    /*
                    * bottom sheet button set on click listener
                    * */
                    btn.setOnClickListener {
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
                                viewModel.addProductTocart(product)
                                productAdded()
                                etQuantity.setText("1")
                            } else if(flag == -1) {
                                context.toast("This variant already exist in your cart")
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
                    bn.setOnClickListener {
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

                    /*
                    * quantity change listener
                    * starting from here
                    * */

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

                }
            }

        })


    }

    private fun productAdded() {
        bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        // context.toast("product added sucessfully to your cart")
    }

    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduct: ImageView = view.findViewById(R.id.imgProductCustomer)
        val txtRating: TextView = view.findViewById(R.id.txtProductRatingCustomer)
        val txtProductName: TextView = view.findViewById(R.id.txtProductNameCustomer)
        val productprice: TextView = view.findViewById(R.id.txtProductPriceCustomer)
        val btnAdd: Button = view.findViewById(R.id.btnAddProductCustomer)
        val recyclerview: RecyclerView = view.findViewById(R.id.productVariantRecyclerView)
    }

}