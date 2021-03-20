package com.tripplleat.trippleattcustomer.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tripplleat.trippleattcustomer.modal.*
import com.tripplleat.trippleattcustomer.ui.home.customer.modal.OrderDetails
import com.tripplleat.trippleattcustomer.ui.home.customer.modal.ProductDetails
import com.tripplleat.trippleattcustomer.ui.home.customer.modal.StoreDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * repository for the customer database handeled by customerViewModel
 */

class CustomerRepo {

    val firestore = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val Fuser = auth.currentUser

    //function to fetch all stores from the database
    fun fetchAllStores(storesList: MutableLiveData<List<StoreDetails>>) {
        val temp_list = ArrayList<StoreDetails>()
        GlobalScope.launch(Dispatchers.IO) {
            firestore.collection("Shop Details").addSnapshotListener { snapshot, error ->
                if(error != null){
                    Log.i("listener", "Listen Failed ${error.message}")
                    return@addSnapshotListener
                }
                if(snapshot != null){
                    val docs = snapshot.documents
                    temp_list.clear()
                    docs.forEach {
                        val storeDetails = it.toObject(StoreDetails::class.java)
                        temp_list.add(storeDetails!!)
                    }
                    Log.i("store","${temp_list.size}")
                    storesList.value = temp_list
                }
            }
        }
    }

    //function to fetch all products from the particular shop from database
    fun fetchAllProduct(id: String, _productList: MutableLiveData<List<String>>, _PackedProductSeller: MutableLiveData<HashMap<String, ArrayList<VariantSeller>>>, _looseProductSeller: MutableLiveData<HashMap<String, ArrayList<VariantSellerLoose>>>) {
        Log.i("store_product",id)
        val temp_list = ArrayList<String>()
        val packedListSeller = HashMap<String,ArrayList<VariantSeller>>()
        val looseListSeller = HashMap<String,ArrayList<VariantSellerLoose>>()
        GlobalScope.launch(Dispatchers.IO) {
            firestore.collection("business/user/$id").addSnapshotListener { snapshot, error ->
                if(error != null){
                    Log.i("listener", "Listen Failed ${error.message}")
                    return@addSnapshotListener
                }
                if(snapshot!= null){
                    temp_list.clear()
                    val docs = snapshot.documents
                    docs.forEach {
                        temp_list.add(it.id)
                        val data = it.toObject(Variants::class.java)
                        if(!data!!.variantList.isEmpty()){
                            packedListSeller[it.id] = data.variantList as ArrayList<VariantSeller>
                        }
                        if(!data!!.variantListLoose.isEmpty()){
                            looseListSeller[it.id] = data.variantListLoose as ArrayList<VariantSellerLoose>
                        }
                    }
                    Log.i("store_product","${temp_list.size}")
                    Log.i("store_Packed","${looseListSeller}")
                    _PackedProductSeller.value = packedListSeller
                    _looseProductSeller.value = looseListSeller
                    _productList.value = temp_list
                }
            }
        }
    }

    //fetch all packed variants from the database of a particualr shop
    fun fetchPackedVariants(_packedProductMain: MutableLiveData<HashMap<String, Variant>>, _PackedProductSeller: MutableLiveData<HashMap<String, ArrayList<VariantSeller>>>,_productList: MutableLiveData<List<String>>) {
        val packedListMain = HashMap<String,Variant>()
        val packedListSeller = _PackedProductSeller.value
        val productList = _productList.value
        var max_size = 0
        GlobalScope.launch(Dispatchers.IO) {
            if (packedListSeller != null) {
                for(i in 0 until productList!!.size){
                    if(packedListSeller.containsKey(productList[i])){
                        val variantList = packedListSeller.get(productList[i])
                        max_size += variantList!!.size
                        for (j in 0 until variantList!!.size){
                            val id = variantList[j].vairantId
                            firestore.collection("variants_list").document(id).addSnapshotListener { snapshot, error ->
                                if(error != null){
                                    Log.i("listener", "Listen Failed ${error.message}")
                                    return@addSnapshotListener
                                }
                                if(snapshot != null){
                                    val variant = snapshot.toObject(Variant::class.java)
                                    packedListMain[id] = variant!!
                                    if(packedListMain.size == max_size){
                                        _packedProductMain.value = packedListMain
                                        Log.i("store_product","${_packedProductMain.value}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun fetchLooseVariants(_looseProductMain: MutableLiveData<HashMap<String, VariantLoose>>,_looseProductSeller: MutableLiveData<HashMap<String, ArrayList<VariantSellerLoose>>>, _productList: MutableLiveData<List<String>>) {
        val looseListMain = HashMap<String,VariantLoose>()
        val looseListSeller = _looseProductSeller.value
        val productList = _productList.value
        var max_size = 0
        GlobalScope.launch(Dispatchers.IO) {
            if (looseListSeller != null) {
                for(i in 0 until productList!!.size){
                    if(looseListSeller.containsKey(productList[i])){
                        val variantList = looseListSeller.get(productList[i])
                        max_size += variantList!!.size
                        for (j in 0 until variantList.size){
                            val id = variantList[j].vairantId
                            firestore.collection("variants_list_loose").document(id).addSnapshotListener { snapshot, error ->
                                if(error != null){
                                    Log.i("listener", "Listen Failed ${error.message}")
                                    return@addSnapshotListener
                                }
                                if(snapshot != null){
                                    val variant = snapshot.toObject(VariantLoose::class.java)
                                    looseListMain[id] = variant!!
                                    if(looseListMain.size == max_size){
                                        _looseProductMain.value = looseListMain
                                        Log.i("store_product_loose","${_looseProductMain.value}")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun addProductTocart(product: ProductDetails,isProductAddedToCart:MutableLiveData<Int>) {
        GlobalScope.launch(Dispatchers.IO) {

            val id = firestore.collection("customer/user/${Fuser?.uid}/cart/allProducts").document().id
            product.productId = id
            product.customerId = Fuser?.uid!!
            firestore.collection("customer/user/${Fuser?.uid}/cart/allProducts").document(id).set(product).addOnSuccessListener {
                isProductAddedToCart.value = 1
            }.addOnFailureListener {
                isProductAddedToCart.value = -1
            }
                }
            }

    //function to fetch products present inside the cart
    fun fetchProductsInCart(_ProductsInCart: MutableLiveData<ArrayList<ProductDetails>>,currentCartList : ArrayList<ProductDetails>) {
        val list = ArrayList<ProductDetails>()
        GlobalScope.launch(Dispatchers.IO) {
            firestore.collection("customer/user/${Fuser?.uid}/cart/allProducts").addSnapshotListener { snapshot, error ->
                if(error != null){
                    Log.i("listener", "Listen Failed ${error.message}")
                    return@addSnapshotListener
                }
                if(snapshot != null){
                    currentCartList.clear()
                    list.clear()
                    val docs = snapshot.documents
                    docs.forEach {
                        val product = it.toObject(ProductDetails::class.java)
                        list.add(product!!)
                    }
                    currentCartList.addAll(list)
                    _ProductsInCart.value = list
                }
            }
        }
    }

    fun removeProductFromCart(product: ProductDetails,isChangesDone : MutableLiveData<Int>) {
        GlobalScope.launch(Dispatchers.IO){
            firestore.collection("customer/user/${Fuser?.uid}/cart/allProducts").document(product.productId).delete().addOnSuccessListener {
                isChangesDone.value = 1
            }.addOnFailureListener {
                isChangesDone.value = 2
            }
        }
    }

    suspend fun fetchCustomerProfile(customerData:MutableLiveData<customeData>){
        val uid:String=FirebaseAuth.getInstance().currentUser?.uid.toString()
        val job =  GlobalScope.launch {
            FirebaseFirestore.getInstance().collection("customer/user/profile").document(uid).get().addOnCompleteListener(
                OnCompleteListener {
                    if (it.isSuccessful){
                        customerData.value=it.result.toObject(customeData::class.java)
                    }
                }).addOnFailureListener(OnFailureListener {
                Log.d("data","error")

            })
        }
        job.join()
    }

    fun updateProductIncart(product: ProductDetails, changesDone: MutableLiveData<Int>) {
        GlobalScope.launch(Dispatchers.IO) {
            firestore.collection("business/user/${product.sellerId}").document(product.productName).addSnapshotListener { snapshot, error ->
                if(error != null){
                    Log.i("listener", "Listen Failed ${error.message}")
                    return@addSnapshotListener
                }
                if(snapshot != null){
                    val variant = snapshot.toObject(Variants::class.java)
                    val packed = variant!!.variantList
                    val loose = variant.variantListLoose
                    if (product.variantUnit.equals("none")){
                        for(i in 0 until packed.size){
                            val packedVariant : VariantSeller = packed[i]
                            if(packedVariant.vairantId.equals(product.variantId)){
                                if(product.orderedQuantity <= packedVariant.Stock.toInt()){
                                    firestore.collection("customer/user/${Fuser?.uid}/cart/allProducts").document(product.productId).update("orderedQuantity",product.orderedQuantity,"totalPrice",product.totalPrice).addOnSuccessListener {
                                        changesDone.value = 3
                                    }.addOnFailureListener {
                                        changesDone.value = 2
                                    }
                                }
                                else{
                                    product.existedQunatity = packedVariant.Stock.toInt()
                                    changesDone.value = 4
                                    return@addSnapshotListener
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun myOrders(_Orders: MutableLiveData<ArrayList<OrderDetails>>,orderCartList : ArrayList<OrderDetails>) {
        val list = ArrayList<OrderDetails>()
        GlobalScope.launch(Dispatchers.IO) {
            firestore.collection("Order Details/${Fuser?.uid}/").addSnapshotListener { snapshot, error ->
                if(error != null){
                    Log.i("listener", "Listen Failed ${error.message}")
                    return@addSnapshotListener
                }
                if(snapshot != null){
                    orderCartList.clear()
                    list.clear()
                    val docs = snapshot.documents
                    docs.forEach {
                        val product = it.toObject(OrderDetails::class.java)
                        list.add(product!!)
                    }
                    orderCartList.addAll(list)
                    _Orders.value = list
                }
            }
        }
    }


}