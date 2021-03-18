package com.tripplleat.trippleattcustomer.repo

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.tripplleat.trippleattcustomer.modal.*
import com.tripplleat.trippleattcustomer.ui.home.seller.listeners.picUploadListener
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

/**
 * This is repository of seller databse and handeled by the seller viewmodel
 */

class HomeRepo {
    val auth = FirebaseAuth.getInstance()
    val Fuser = auth.currentUser
    val imageDatabase = FirebaseStorage.getInstance().getReference("Images")
    val fireStore = FirebaseFirestore.getInstance()
    val idList = ArrayList<String>()

    //functions to add the packed product in database and in seller database
    suspend fun uploadSellerProductPacked(
        productName: String,
        category: String,
        variantList_Local: ArrayList<Variant>,// This is the list of variant which will be uploaded
        variantListSeller: ArrayList<VariantSeller>,// This is list of seller which will b uploaded in seller database and this list contanins stock and selling price
        imageList: ArrayList<Uri>,
        isUploaded: MutableLiveData<Int>
    ) {
        GlobalScope.launch(Dispatchers.Main) {

            for (i in 0 until imageList.size) {
                val storageRef =
                    imageDatabase.child(productName).child(variantList_Local[i].variant)
                        .child("img")
                storageRef.putFile(imageList[i]).addOnSuccessListener { taskSnapshot ->
                    storageRef.downloadUrl.addOnSuccessListener {
                        variantList_Local[i].image = it.toString()
                        val id = fireStore.collection("variants_list").document().id
                        variantList_Local[i].id = id
                        variantListSeller[i].vairantId = id
                        idList.add(id)
                        Log.i("product_image_inside", "${variantList_Local[i]}")
                        fireStore.collection("variants_list").document(id).set(variantList_Local[i])
                            .addOnSuccessListener {
                                val productData = hashMapOf(
                                    "productName" to productName,
                                    "category" to category,
                                    "variants" to idList as List<String>
                                )
                                fireStore.collection("master_product_list").document(productName)
                                    .set(productData).addOnSuccessListener {
                                        Log.i("product_masterList", "${variantList_Local[i]}")
                                        fireStore.collection("business/user/${Fuser?.uid}")
                                            .document(productName).set(
                                                Variants(
                                                    productName,
                                                    category,
                                                    variantListSeller as List<VariantSeller>,
                                                    emptyList()
                                                )
                                            ).addOnSuccessListener {
                                                isUploaded.value = i
                                            }.addOnFailureListener {
                                                val ref = FirebaseStorage.getInstance()
                                                    .getReferenceFromUrl(variantList_Local[i].image)
                                                ref.delete()
                                                fireStore.collection("variants_list").document(id)
                                                    .delete()
                                                fireStore.collection("master_product_list")
                                                    .document(productName).delete()
                                                isUploaded.value = -1
                                                Log.i("Exception", "${it.message}")
                                            }
                                    }.addOnFailureListener {
                                        val ref = FirebaseStorage.getInstance()
                                            .getReferenceFromUrl(variantList_Local[i].image)
                                        ref.delete()
                                        fireStore.collection("variants_list").document(id).delete()
                                        isUploaded.value = -1
                                        Log.i("Exception", "${it.message}")
                                    }
                            }.addOnFailureListener { exception ->
                                val ref = FirebaseStorage.getInstance()
                                    .getReferenceFromUrl(variantList_Local[i].image)
                                ref.delete().addOnSuccessListener {
                                    Log.i("product_failure", "image deleted")
                                    isUploaded.value = -1
                                    Log.i("Exception", "${exception.message}")
                                }
                            }
                    }.addOnFailureListener {
                        isUploaded.value = -1
                        Log.i("Exception", "${it.message}")
                    }
                }.await()
            }

        }

    }

    //functions to add the loose product in database and in seller database
    suspend fun uploadSellerProductLoose(
        local: VariantLoose,
        seller: VariantSellerLoose,
        Image: Uri,
        isUploaded: MutableLiveData<Int>
    ) {
        GlobalScope.launch(Dispatchers.Main) {
            val storageRef = imageDatabase.child(local.productName).child("loose")
                .child("img")
            storageRef.putFile(Image).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    local.image = it.toString()
                    val id = fireStore.collection("variants_list_loose").document().id
                    seller.vairantId = id
                    local.id = id
                    fireStore.collection("variants_list_loose").document(id).set(local)
                        .addOnSuccessListener {
                            val productData = hashMapOf(
                                "productName" to local.productName,
                                "category" to local.category,
                                "variants_Loose" to listOf<String>(id)
                            )
                            fireStore.collection("master_product_list").document(local.productName)
                                .set(productData).addOnSuccessListener {
                                    fireStore.collection("business/user/${Fuser?.uid}")
                                        .document(local.productName).set(
                                            Variants(
                                                local.productName,
                                                local.category,
                                                emptyList(),
                                                listOf(seller)
                                            )
                                        ).addOnSuccessListener {
                                            isUploaded.value = 1
                                        }.addOnFailureListener {
                                            isUploaded.value = 2
                                            Log.i("Exception", "${it.message}")
                                        }
                                }.addOnFailureListener {
                                    val ref =
                                        FirebaseStorage.getInstance()
                                            .getReferenceFromUrl(local.image)
                                    ref.delete()
                                    fireStore.collection("variants_list").document(id).delete()
                                    isUploaded.value = -1
                                    Log.i("Exception", "${it.message}")

                                }
                        }.addOnFailureListener { exception ->
                            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(local.image)
                            ref.delete().addOnSuccessListener {
                                Log.i("product_failure", "image deleted")
                                isUploaded.value = -1
                                Log.i("Exception", "${exception.message}")
                            }
                        }
                }
            }.addOnFailureListener {
                isUploaded.value = -1
                Log.i("Exception", "${it.message}")
            }
        }
    }

    //function to fetch all product from the database

    fun listenProductChange(
        productList: ArrayList<String>,
        _productName: MutableLiveData<ArrayList<String>>
    ) {
        fireStore.collection("master_product_list").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.i("listener", "Listen Failed ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val docs = snapshot.documents
                productList.clear()
                docs.forEach {
                    val product = it.getString("productName")
                    productList.add(product!!)
                }
                _productName.value = productList
            }
        }
    }

    // function to fetch the all variants of packed product
    fun listenVariantChange(
        variantsList: ArrayList<Variant>,
        _variants: MutableLiveData<ArrayList<Variant>>
    ) {
        fireStore.collection("variants_list").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.i("listener", "Listen Failed ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val docs = snapshot.documents
                variantsList.clear()
                docs.forEach {
                    val variant = it.toObject(Variant::class.java)
                    variantsList.add(variant!!)
                }
                _variants.value = variantsList
            }
        }
    }

    // function to fetch the all variants of loose product
    fun listenVariantLooseChange(
        variantsListLoose: ArrayList<VariantLoose>,
        _variantsLoose: MutableLiveData<ArrayList<VariantLoose>>
    ) {
        fireStore.collection("variants_list_loose").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.i("listener", "Listen Failed ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val docs = snapshot.documents
                variantsListLoose.clear()
                docs.forEach {
                    val variant = it.toObject(VariantLoose::class.java)
                    variantsListLoose.add(variant!!)
                }
                _variantsLoose.value = variantsListLoose
            }
        }
    }

    //function to add a packed variant in a particular product
    fun addVariantInProduct(
        variantLocal: Variant,
        variantSeller: VariantSeller,
        image: Uri,
        isVariantUploaded: MutableLiveData<Int>
    ) {
        GlobalScope.launch {
            val storageRef =
                imageDatabase.child(variantLocal.productName).child(variantLocal.variant)
                    .child("img")
            storageRef.putFile(image).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    variantLocal.image = it.toString()
                    val id = fireStore.collection("variants_list").document().id
                    variantSeller.vairantId = id
                    variantLocal.id = id
                    fireStore.collection("variants_list").document(id).set(variantLocal)
                        .addOnSuccessListener {
                            fireStore.collection("master_product_list")
                                .document(variantLocal.productName)
                                .update("variants", FieldValue.arrayUnion(id))
                                .addOnSuccessListener {
                                    fireStore.collection("business/user/${Fuser?.uid}")
                                        .document(variantLocal.productName).get()
                                        .addOnSuccessListener { document ->
                                            if (document.exists()) {
                                                fireStore.collection("business/user/${Fuser?.uid}")
                                                    .document(variantLocal.productName).update(
                                                        "variantList",
                                                        FieldValue.arrayUnion(variantSeller)
                                                    ).addOnSuccessListener {
                                                        isVariantUploaded.value = 1
                                                    }.addOnFailureListener {
                                                        isVariantUploaded.value = -1
                                                        Log.i(
                                                            "product_variant_error",
                                                            "${it.message}"
                                                        )
                                                    }
                                            } else {
                                                fireStore.collection("business/user/${Fuser?.uid}")
                                                    .document(variantLocal.productName).set(
                                                        Variants(
                                                            variantLocal.productName,
                                                            variantLocal.category,
                                                            listOf(variantSeller),
                                                            emptyList()
                                                        )
                                                    ).addOnSuccessListener {
                                                        isVariantUploaded.value = 1
                                                    }.addOnFailureListener {
                                                        isVariantUploaded.value = -1
                                                    }
                                            }
                                        }
                                }
                        }
                }
            }
        }
    }

    //function to add a loose variant in a particular product
    fun addVariantInProductLoose(
        local: VariantLoose,
        seller: VariantSellerLoose,
        looseDialogImage: Uri,
        variantUploaded: MutableLiveData<Int>
    ) {
        GlobalScope.launch {
            val storageRef =
                imageDatabase.child(local.productName).child("loose")
                    .child("img")
            storageRef.putFile(looseDialogImage).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener {
                    local.image = it.toString()
                    val id = fireStore.collection("variants_list_loose").document().id
                    seller.vairantId = id
                    local.id = id
                    fireStore.collection("variants_list_loose").document(id).set(local)
                        .addOnSuccessListener {
                            fireStore.collection("master_product_list")
                                .document(local.productName)
                                .update("variants", FieldValue.arrayUnion(id))
                                .addOnSuccessListener {
                                    fireStore.collection("business/user/${Fuser?.uid}")
                                        .document(local.productName).get()
                                        .addOnSuccessListener { document ->
                                            if (document.exists()) {
                                                fireStore.collection("business/user/${Fuser?.uid}")
                                                    .document(local.productName).update(
                                                        "variantListLoose",
                                                        FieldValue.arrayUnion(seller)
                                                    ).addOnSuccessListener {
                                                        variantUploaded.value = 1
                                                    }.addOnFailureListener {
                                                        variantUploaded.value = -1
                                                        Log.i(
                                                            "product_variant_error",
                                                            "${it.message}"
                                                        )
                                                    }
                                            } else {
                                                fireStore.collection("business/user/${Fuser?.uid}")
                                                    .document(local.productName).set(
                                                        Variants(
                                                            local.productName, local.category,
                                                            emptyList(), listOf(seller)
                                                        )
                                                    ).addOnSuccessListener {
                                                        variantUploaded.value = 1
                                                    }.addOnFailureListener {
                                                        variantUploaded.value = -1
                                                    }
                                            }
                                        }
                                }
                        }
                }
            }
        }
    }


    //function to add a packed variant in seller database
    suspend fun addVariantInSeller(
        price: String,
        stock: String,
        currentVariant: Variant,
        isVariantUploaded: MutableLiveData<Int>
    ) {
        Log.i("product_check", "user has reached")
        fireStore.collection("business/user/${Fuser?.uid}").document(currentVariant.productName)
            .get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val product = document.toObject(Variants::class.java)
                    val list: List<VariantSeller> = product!!.variantList
                    for (i in 0 until list.size) {
                        if (list[i].vairantId == currentVariant.id) {
                            Log.i("product", "mil gaya shab")
                            isVariantUploaded.value = 2
                            return@addOnSuccessListener
                        }
                    }
                    Log.i("product", " nahi mil shab")
                    fireStore.collection("business/user/${Fuser?.uid}")
                        .document(currentVariant.productName).update(
                            "variantList",
                            FieldValue.arrayUnion(
                                VariantSeller(
                                    true,
                                    price,
                                    stock,
                                    currentVariant.id
                                )
                            )
                        ).addOnSuccessListener {
                            isVariantUploaded.value = 1
                        }.addOnFailureListener {
                            isVariantUploaded.value = -1
                            Log.i("product_variant_error", "${it.message}")
                        }


                } else {
                    fireStore.collection("business/user/${Fuser?.uid}")
                        .document(currentVariant.productName).set(
                            Variants(
                                currentVariant.productName,
                                currentVariant.category,
                                listOf(VariantSeller(true, price, stock, currentVariant.id)),
                                emptyList()
                            )
                        ).addOnSuccessListener {
                            isVariantUploaded.value = 1
                        }.addOnFailureListener {
                            isVariantUploaded.value = -1
                            Log.i("product_variant_error", "${it.message}")
                        }
                }
            }

    }

    //FUNCTION TO ADD the loose variant of a product in seller database
    fun addVariantLooseInSeller(
        price: String,
        stock: String,
        unit: String,
        currentVariantLoose: VariantLoose,
        variantUploaded: MutableLiveData<Int>
    ) {
        Log.i("product_check", "user has reached In Loose")
        fireStore.collection("business/user/${Fuser?.uid}")
            .document(currentVariantLoose.productName)
            .get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val product = document.toObject(Variants::class.java)
                    val list: List<VariantSellerLoose> = product!!.variantListLoose
                    for (i in 0 until list.size) {
                        if (list[i].vairantId == currentVariantLoose.id) {
                            Log.i("product", "mil gaya shab _ ${list[i].vairantId}")
                            variantUploaded.value = 2
                            return@addOnSuccessListener
                        }
                    }
                    Log.i("product", " nahi mil shab")
                    fireStore.collection("business/user/${Fuser?.uid}")
                        .document(currentVariantLoose.productName).update(
                            "variantListLoose",
                            FieldValue.arrayUnion(
                                VariantSellerLoose(
                                    true,
                                    price,
                                    stock,
                                    unit,
                                    currentVariantLoose.id
                                )
                            )
                        ).addOnSuccessListener {
                            variantUploaded.value = 1
                        }.addOnFailureListener {
                            variantUploaded.value = -1
                            Log.i("product_variant_error", "${it.message}")
                        }


                } else {
                    fireStore.collection("business/user/${Fuser?.uid}")
                        .document(currentVariantLoose.productName).set(
                            Variants(
                                currentVariantLoose.productName, currentVariantLoose.category,
                                emptyList(),
                                listOf(
                                    VariantSellerLoose(
                                        true,
                                        price,
                                        stock,
                                        unit,
                                        currentVariantLoose.id
                                    )
                                )
                            )
                        ).addOnSuccessListener {
                            variantUploaded.value = 1
                        }.addOnFailureListener {
                            variantUploaded.value = -1
                            Log.i("product_variant_error", "${it.message}")
                        }
                }
            }
    }


    //function to fetch the published varints
    fun myPublishedVariants(
        publishedPackedList: MutableLiveData<List<VariantSeller>>,
        loosePublishedList: MutableLiveData<List<VariantSellerLoose>>
    ) {
        val packed = ArrayList<VariantSeller>()
        val loose = ArrayList<VariantSellerLoose>()
        fireStore.collection("business/user/${Fuser?.uid}").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.i("listener", "Listen Failed ${error.message}")
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val docs = snapshot.documents
                packed.clear()
                loose.clear()
                docs.forEach {
                    val product = it.toObject(Variants::class.java)
                    val packed_list = product!!.variantList
                    for (i in 0 until packed_list.size) {
                        if (packed_list[i].published)
                            packed.add(packed_list[i])
                        Log.i("product_list_packed", "${packed_list[i]}")
                    }
                    val loose_list = product!!.variantListLoose
                    Log.i("product_list_Loose", "${loose_list.size}")
                    for (i in 0 until loose_list.size) {
                        if (loose_list[i].published)
                            loose.add(loose_list[i])
                        Log.i("product_list_Loose", "${loose_list[i]}")
                    }
                }
                Log.i("product_packed", "${packed}")
                publishedPackedList.value = packed
                loosePublishedList.value = loose
            }
        }
    }


    //function to update a apcked variant in seller database
    fun updateMyVariantPacked(price: String, stock: String, id: String, product: String) {
        fireStore.collection("business/user/${Fuser?.uid}").document(product).get()
            .addOnSuccessListener { snapshot ->
                val obj = snapshot.toObject(Variants::class.java)
                val list = obj!!.variantList
                for (i in 0 until list.size) {
                    if (list[i].vairantId.equals(id)) {
                        var variant = list[i]
                        val sp = variant.sellingPrice
                        val stk = variant.Stock
                        fireStore.collection("business/user/${Fuser?.uid}").document(product)
                            .update("variantList", FieldValue.arrayRemove(variant))
                            .addOnSuccessListener {
                                if (stock.isEmpty())
                                    variant = VariantSeller(true, price, stk, id)
                                else if (price.isEmpty())
                                    variant = VariantSeller(true, sp, stock, id)
                                else
                                    variant = VariantSeller(true, price, stock, id)
                                fireStore.collection("business/user/${Fuser?.uid}")
                                    .document(product)
                                    .update("variantList", FieldValue.arrayUnion(variant))
                            }.addOnFailureListener {
                                Log.i("product_update_exc", it.message!!)
                            }
                    }
                }
            }
    }

    //function to update a loose variant in seller database
    fun updateMyVariantLoose(price: String, stock: String, id: String, product: String) {
        fireStore.collection("business/user/${Fuser?.uid}").document(product).get()
            .addOnSuccessListener { snapshot ->
                val obj = snapshot.toObject(Variants::class.java)
                val list = obj!!.variantListLoose
                for (i in 0 until list.size) {
                    if (list[i].vairantId.equals(id)) {
                        var variant = list[i]
                        val sp = variant.sellingPrice
                        val stk = variant.Stock
                        val unit = variant.sellingUnit
                        Log.i("product_loose", "$variant")
                        fireStore.collection("business/user/${Fuser?.uid}").document(product)
                            .update("variantListLoose", FieldValue.arrayRemove(variant))
                            .addOnSuccessListener {
                                if (stock.isEmpty())
                                    variant = VariantSellerLoose(true, price, stk, unit, id)
                                else if (price.isEmpty())
                                    variant = VariantSellerLoose(true, sp, stock, unit, id)
                                else
                                    variant = VariantSellerLoose(true, price, stock, unit, id)
                                fireStore.collection("business/user/${Fuser?.uid}")
                                    .document(product)
                                    .update("variantListLoose", FieldValue.arrayUnion(variant))
                            }.addOnFailureListener {
                                Log.i("product_update_exc", it.message!!)
                            }
                    }
                }
            }
    }


    //update Business Profile picture
    fun updatePicture(path: String, listener: picUploadListener, image: Uri) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val firebaseStorage =
            FirebaseStorage.getInstance().getReference().child("business").child(uid)
        val ImageRef = firebaseStorage.child(
            image.getLastPathSegment().toString()
        )
        ImageRef.putFile(image).addOnCompleteListener(OnCompleteListener {
            if (it.isSuccessful) {
                ImageRef.downloadUrl.addOnSuccessListener {
                    val image1: MutableMap<String, Any> = HashMap()
                    image1[path] = it.toString()
                    FirebaseFirestore.getInstance().collection("business/user/profile")
                        .document(uid).set(image1, SetOptions.merge()).addOnCompleteListener(
                        OnCompleteListener {
                            if (it.isSuccessful) {
                                listener.onSuccess()
                            } else {
                                listener.onFailed(
                                    it.exception
                                        ?.message.toString()
                                )
                            }
                        })
                }
            } else {
                listener.onFailed(it.exception?.message.toString())
            }
        })
    }

    //    Business Profile update
    fun updateBusiProfile(valuesToChange: String, attributes: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val image1: MutableMap<String, Any> = HashMap()
        image1[attributes] = valuesToChange
        FirebaseFirestore.getInstance().collection("business/user/profile").document(uid)
            .set(image1, SetOptions.merge())
    }


    //    Fetching data from firestore business profile
    suspend fun getBusiProfileData(busi1: MutableLiveData<businessData>) {
        val uid: String = FirebaseAuth.getInstance().currentUser?.uid.toString()
        val job = GlobalScope.launch {
            FirebaseFirestore.getInstance().collection("business/user/profile").document(uid).get()
                .addOnCompleteListener(OnCompleteListener {
                    if (it.isSuccessful) {
                        busi1.value = it.result?.toObject(businessData::class.java)
                    }
                }).addOnFailureListener(OnFailureListener {
                Log.d("data", "error")

            })
        }
        job.join()

    }

}

