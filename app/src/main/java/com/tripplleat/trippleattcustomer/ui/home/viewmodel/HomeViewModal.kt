package com.tripplleat.trippleattcustomer.ui.home.viewmodel

import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.tripplleat.trippleattcustomer.modal.*
import com.tripplleat.trippleattcustomer.repo.HomeRepo
import com.tripplleat.trippleattcustomer.ui.home.customer.activity.profileFragment
import com.tripplleat.trippleattcustomer.ui.home.seller.listeners.ActivityForResultListener
import com.tripplleat.trippleattcustomer.ui.home.seller.listeners.picUploadListener
import com.tripplleat.trippleattcustomer.util.toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This is viewmodel for the Home activity of the seller and all the live data and the repo functions are controlled
 * from here.
 */

class HomeViewModal(private val repo : HomeRepo, private val application : Application) : ViewModel() {

    lateinit var VariantObject : Variants

    val firebaseAuth = FirebaseAuth.getInstance()

    var businame:String?=null
    var email:String?=null
    var busicategory:String?=null
    var website:String?=null
    var fulladdress:String?=null

    var addProduct = MutableLiveData<Boolean>()
    var _variantTypeLoose = MutableLiveData<String>()
    var allVaraints = MutableLiveData<Int>(0)
    var barCodeClicked = MutableLiveData<Boolean>()
    var isUploaded = MutableLiveData<Int>()
    var isVariantUploaded = MutableLiveData<Int>()
    var dialogIsclicked = MutableLiveData<Boolean>()
    var submitClicked = MutableLiveData<Boolean>()
    var _productName = MutableLiveData<ArrayList<String>>()
    var _variants = MutableLiveData<ArrayList<Variant>>()
    var _variantsLoose = MutableLiveData<ArrayList<VariantLoose>>()
    var _publishedPacked = MutableLiveData<List<VariantSeller>>()
    var _publishedLoose = MutableLiveData<List<VariantSellerLoose>>()
    var _actvariantList = MutableLiveData<ArrayList<Any>>()

    //variables for publishedlist
    var start = 0

    //this list is for storing the data in application not to upload at database
    var productList = ArrayList<String>()
    var variantsList = ArrayList<Variant>()
    var variantsListLoose = ArrayList<VariantLoose>()
    var current_variant : Variant? = null
    var current_variant_loose : VariantLoose? = null//till this

    var busidata=MutableLiveData<businessData>()
    var productName : String? = null
    var category : String? = null
    var category_position = -1
    var mrp : String? = null
    var sellingPrice : String? = null
    var variant: String? = null
    var stock : String? = null
    var barcode : String? = null
    var variantTypeLoose : String? = null
    var Image : Uri? = null
    var variantUnit : String = "Kg"

    var modeOfBarcode : Int = 1
    var modeOfUploadingProduct = 0

    var activityresultListener : ActivityForResultListener? = null
    var activityresultListenerLoose : ActivityForResultListener? = null
    var bitmap : Bitmap? = null
    var packed : Boolean = true
    val variantList_Local = ArrayList<Variant>()
    val variantList_seller = ArrayList<VariantSeller>()
    val variantListLoose = ArrayList<VariantLoose>()
    val imageList = ArrayList<Uri>()

    // variables to store packed variant for product *see AddVariantToProductDialog.kt
    var packedDialogMrp : String? = null
    var packedDialogSellingPrice : String? = null
    var packedDialogVariant : String? = null
    var packedDialogStock : String? = null
    var packedDialogImage : Uri? = null

    //variables to stpre loose variant for product * seeAddVariantToProductDialogLoose.kt
    var looseDialogSellingPrice : String? = null
    var looseDialogStock : String? = null
    var looseDialogImage :Uri? = null
    var looseDialogVariantUnit : String = "Kg"
    var pictureUploadListener: picUploadListener?=null


    init {
        viewModelScope.launch(Dispatchers.IO) {
            repo.listenProductChange(productList,_productName)//function to fetch all product list from databse
            repo.listenVariantChange(variantsList,_variants)// function to fetch all packed variant from the database
            repo.listenVariantLooseChange(variantsListLoose,_variantsLoose)// function to fetch all loose variant from the database
            repo.myPublishedVariants(_publishedPacked,_publishedLoose)// function to fetch the published variant from the seller databse
        }
    }


    public fun onClickAddProduct(view: View){
        addProduct.value = true
    }

    // variant Unit select from loose Product
    public fun onLooseKgSelected(view: View){
        variantTypeLoose = "kg"
        _variantTypeLoose.value = "kg"
    }

    // variant Unit select from loose Product
    public fun onLooseLiterSelected(view: View){
        variantTypeLoose = "liter"
        _variantTypeLoose.value = "liter"
    }



    //Adding Packed variants by checking all its fields
    public fun onClickAddVAriant(view: View){
        var check = true

        if(allVaraints.value == 0) {
            if (category.isNullOrEmpty() || productName.isNullOrEmpty() || variant.isNullOrEmpty() || barcode.isNullOrEmpty() || mrp.isNullOrEmpty() || sellingPrice.isNullOrEmpty() || stock.isNullOrEmpty() ){
                application.toast("please enter all the valid details")
               check = false
            }
            else if (imageList.size == 0 || (imageList.get(allVaraints.value!!).equals(""))){
                application.toast("please select the product image")
                check = false
            }
        }
        else if(allVaraints.value != 0){

            val temp : Variant = variantList_Local[allVaraints.value!!]
            val selTemp : VariantSeller = variantList_seller[allVaraints.value!!]

            if(temp.variant.isEmpty() || temp.barcode.isEmpty() || temp.mrp.isEmpty() || selTemp.sellingPrice.isEmpty() ||  selTemp.Stock.isEmpty()){
                application.toast("please enter all the valid details")
                check = false
            }
            else if (((imageList.size ) != (allVaraints.value!! + 1)) || (imageList.get(allVaraints.value!!).equals(""))){
                application.toast("please select the product image")
                check = false
            }
        }
            if(check){

                if(allVaraints.value == 0 && variantList_Local.size == 0){
                    variantList_Local.add(Variant(barcode!!,category!!,"will be added","added later",mrp!!,productName!!,variant!!))
                    variantList_seller.add(VariantSeller(true,sellingPrice!!,stock!!,"added later"))
                   // VariantObject = Variants(productName!!,"oil",variantList_seller)
                }
                var temp2 = allVaraints.value!!
                temp2++
                allVaraints.value = temp2
                variantList_Local.add(Variant("",category!!,"","","",productName!!,""))
                variantList_seller.add(VariantSeller(true,"","",""))
                Log.i("check_variant_size","${variantList_Local.size}")
            }
    }




    //Image taken From File or Internal Storage
    public fun onClickProductImageFile(view: View){
        if(packed){
            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            activityresultListener?.StartActivityForResult(intent,100)
        }
        else{
            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            activityresultListenerLoose?.StartActivityForResult(intent,100)
        }

    }




    // Image taken by camera
    public fun onClickProductImageCamera(view: View){
        if(packed){
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            activityresultListener?.StartActivityForResult(intent,101)
        }
        else{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            activityresultListenerLoose?.StartActivityForResult(intent,101)
        }


    }

    public fun barCodeReaderExtra(mode : Int){
        barCodeClicked.value = true
        modeOfBarcode = mode
    }


    //BAr code Method for packed Product
    public fun onClickBarCodeReader(view: View){
        barCodeClicked.value = true
        modeOfBarcode = 1
    }



    // BArcode Method For Loose Product
    public fun onClickBarCodeReaderLoose(view: View){
        barCodeClicked.value = true
        modeOfBarcode = 2
    }



    // Final OnSubmit Button of variants
    fun onClickSubmitForPublish(view: View) {
        viewModelScope.launch {
            if (packed) {
                if(checkvariantsData()){
                    submitClicked.value = true
                    if(allVaraints.value == 0 && variantList_Local.size == 0){
                        variantList_Local.add(Variant(barcode!!,category!!,"will be added","added later",mrp!!,productName!!,variant!!))
                        variantList_seller.add(VariantSeller(true,sellingPrice!!,stock!!,"added later"))
                    }
                    VariantObject = Variants(productName!!,category!!,variantList_seller, emptyList())
                    submitClicked.value = true
                        repo.uploadSellerProductPacked(productName!!, category!!, variantList_Local,variantList_seller, imageList,isUploaded)

                }
            } else {
                if (checkvariantsData()){
                        val local = com.tripplleat.trippleattcustomer.modal.VariantLoose(
                            category!!,
                            "will be added",
                            "will be added",
                            productName!!,
                            variantUnit
                        )
                        val seller = com.tripplleat.trippleattcustomer.modal.VariantSellerLoose(true,
                            sellingPrice!!,
                            stock!!,
                            variantUnit,
                            "will be added"
                        )
                    submitClicked.value = true
                        addProductLoose(local,seller)
                }
            }
        }

    }

    private fun addProductLoose(local: VariantLoose, seller: VariantSellerLoose) {
        viewModelScope.launch {
            val current_variant = local.productName
            for(i in 0 until  variantsListLoose.size){
                if(variantsListLoose[i].productName.equals(current_variant,true)){
                    Log.i("product_variant_exist","True")
                    isUploaded.value = 3
                    return@launch
                }
            }
            repo.uploadSellerProductLoose(local,seller,Image!!,isUploaded)
        }
    }


    //Checking final variants Fields
    private suspend  fun checkvariantsData() : Boolean{
        var check = true
        if(packed){
            if(allVaraints.value == 0) {
                if (category.isNullOrEmpty() || productName.isNullOrEmpty() || variant.isNullOrEmpty() || barcode.isNullOrEmpty() || mrp.isNullOrEmpty() || sellingPrice.isNullOrEmpty() || stock.isNullOrEmpty() ){
                    application.toast("please enter all the valid details")
                    check = false
                }
                else if (imageList.size == 0 || (imageList.get(allVaraints.value!!).equals(""))){
                    application.toast("please select the product image")
                    check = false
                }
            }
            else{
                val temp : Variant = variantList_Local[allVaraints.value!!]
                val selTemp : VariantSeller = variantList_seller[allVaraints.value!!]

                if(temp.variant.isEmpty() || temp.barcode.isEmpty() || temp.mrp.isEmpty() || selTemp.sellingPrice.isEmpty() ||  selTemp.Stock.isEmpty()){
                    application.toast("please enter all the valid details")
                    check = false
                }
                else if (((imageList.size ) != (allVaraints.value!! + 1)) || (imageList.get(allVaraints.value!!).equals(""))){
                    application.toast("please select the product image")
                    check = false
                }
            }
        }
        else{
            if(productName.isNullOrEmpty() || sellingPrice.isNullOrEmpty() || stock.isNullOrEmpty() || category.isNullOrEmpty()){
                application.toast("please fill all the details in feild")
                check = false
            }
            else if(Image == null){
                application.toast("please select the image")
                check = false
            }
        }
        return check
    }

    //This function will clear the list of variants
    override fun onCleared() {
        super.onCleared()
        variantList_Local.clear()
    }

    //This function work when all packed  and loose variant is fetched and product is alos fetched and
    //then this function will add the data in list and make it one
    fun addProductAndvariant() {
        val al = ArrayList<Any>()
        al.addAll(productList)
        al.addAll(variantsList)
        al.addAll(variantsListLoose)
        _actvariantList.value = al
    }

    // function to find the product with the help of barcode
    suspend fun findProductWithBarcode(barcode: String) : Variant?{
            for(i in 0 until variantsList.size){
                if(variantsList[i].barcode.equals(barcode)){
                    return variantsList[i]
                }
            }
        return null;
    }
    //This function will add a new variant int seller database
    fun addNewVariantToSeller(price : String, stock : String){
        viewModelScope.launch {
            if(current_variant != null){
                repo.addVariantInSeller(price,stock,current_variant!!,isVariantUploaded)
                dialogIsclicked.value = true
            }
            else{
                Log.i("poduct_error","No such product exist")
            }
        }
    }
    // function to add packed varaint of a product
    fun addVariantToProduct(variant_local : Variant, variantSeller : VariantSeller){
        val current_variant = variant_local.variant
        for(i in 0 until variantsList.size){
            if(variantsList[i].productName.equals(variant_local.productName,true) && variantsList[i].variant.equals(current_variant,true)){
                Log.i("product_variant_exist","True")
                isVariantUploaded.value = 2
                return
            }
        }
        repo.addVariantInProduct(variant_local,variantSeller,packedDialogImage!!,isVariantUploaded)
    }

    // function to add loose varaint of a product
    fun addVariantToProductLoose(local: VariantLoose, seller: VariantSellerLoose) {
        val current_variant = local.productName
        for(i in 0 until  variantsListLoose.size){
            if(variantsListLoose[i].productName.equals(current_variant,true)){
                Log.i("product_variant_exist","True")
                isVariantUploaded.value = 2
                return
            }
        }
        repo.addVariantInProductLoose(local,seller,looseDialogImage!!,isVariantUploaded)
    }

    // function to add loose varaint of a product in seller databse
    fun addNewVariantLooseToSeller(price: String, stock: String, unit: String) {
        viewModelScope.launch {
            if(current_variant_loose!= null){
                repo.addVariantLooseInSeller(price,stock,unit,current_variant_loose!!,isVariantUploaded)
                dialogIsclicked.value = true
            }
        }
    }
    // function to update the packed variant present in the published list
    fun updateMyVariantPacked(price : String, stock : String, id : String, product : String){
        if(price.isEmpty() && stock.isEmpty())
            application.toast("please Update any value Stock or Price")
        else
            repo.updateMyVariantPacked(price,stock,id,product)
    }
    // function to update the packed variant present in the loose list
     fun updateMyVariantLoose(price : String, stock : String, id : String, product : String){
         if(price.isEmpty() && stock.isEmpty())
             application.toast("please Update any value Stock or Price")
         else
             repo.updateMyVariantLoose(price,stock,id,product)
     }

    fun goProfileActivity(v:View){
        val intent= Intent(v.context, profileFragment::class.java)
        v.context.startActivity(intent)
    }

    //    Upload photo for business profile
    fun uploadPicture(path: String,image:Uri){
        pictureUploadListener?.onStarted()
        repo.updatePicture(path,pictureUploadListener!!,image)
    }

    fun checkingFilledDetails(){
        viewModelScope.launch {
            repo.getBusiProfileData(busidata)
        }
    }
    // update business basic details
    fun updateBusiProfile(view: View){
        if (!businame.isNullOrEmpty()) {
            repo.updateBusiProfile(businame!!,"businame")
        }
        if (!category.isNullOrEmpty()) {
            repo.updateBusiProfile(category!!,"category")
        }
        if (!email.isNullOrEmpty()) {
            repo.updateBusiProfile(email!!,"emailid")
        }
        if (!website.isNullOrEmpty()) {
            repo.updateBusiProfile(website!!,"website")
        }
        if (!fulladdress.isNullOrEmpty()) {
            repo.updateBusiProfile(fulladdress!!,"fulladdress")
        }
    }

}