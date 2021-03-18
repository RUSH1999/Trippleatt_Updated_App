package com.tripplleat.trippleattcustomer.ui.home.customer.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.ui.home.customer.modal.StoreDetails
import com.tripplleat.trippleattcustomer.ui.home.customer.viewmodel.CustomerViewModel

/*
* This is adapter which is used by the recycler view to show the stores present in the database
* */

class StoresRecyclerAdapter(var context: Context,var viewModel : CustomerViewModel, var storeList : List<StoreDetails>) : RecyclerView.Adapter<StoresRecyclerAdapter.StoresViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoresViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.store_design,parent,false)
        return StoresViewHolder(view)
    }

    override fun getItemCount(): Int {
        return storeList.size
    }

    override fun onBindViewHolder(holder: StoresViewHolder, position: Int) {

        val store = storeList[position]
        holder.txtStoreName.text = store.shopName
        holder.txtStoreRating.text = store.rating.toString()
        holder.txtStoreType.text = store.category
        holder.txtStoreDistance.text = "near"
        Picasso.get().load(store.image1).error(R.drawable.home).into(holder.imgStore)

        holder.imgStore.setOnClickListener {
            viewModel.currentStore = storeList[position]
            it.findNavController().navigate(R.id.action_home_fragment_to_storeProductsFragment)
        }
    }

    class StoresViewHolder(view : View) : RecyclerView.ViewHolder(view){
        val imgStore : ImageView = view.findViewById(R.id.imgStoreImage)
        val txtStoreName : TextView = view.findViewById(R.id.txtStoreName)
        val txtStoreType : TextView = view.findViewById(R.id.txtStoreType)
        val txtStoreRating : TextView = view.findViewById(R.id.txtStoreRating)
        val txtStoreDistance : TextView = view.findViewById(R.id.txtDistance)
    }

}