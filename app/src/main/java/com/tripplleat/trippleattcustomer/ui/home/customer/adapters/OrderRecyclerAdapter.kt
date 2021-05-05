package com.tripplleat.trippleattcustomer.ui.home.customer.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.ui.home.customer.modal.OrderDetails
import com.tripplleat.trippleattcustomer.ui.home.customer.viewmodel.CustomerViewModel

class OrderRecyclerAdapter(
    val context: Context,
    val orderList: ArrayList<OrderDetails>,
    val viewModel: CustomerViewModel
    ):RecyclerView.Adapter<OrderRecyclerAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.place_order_fragment, parent, false)
        return OrderRecyclerAdapter.ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val myproduct = orderList[position]
        val name= holder.pname_input.text
        val add=holder.paddress_input.text
        val land= holder.plandmark_input.text
        holder.btn.setOnClickListener{
            if(name.isNullOrEmpty() && add.isNullOrEmpty() && land.isNullOrEmpty()){
                Toast.makeText(context,"Enter Information",Toast.LENGTH_SHORT).show()
            }

        }

    }



    override fun getItemCount(): Int {
        return orderList.size
    }

    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val btn:Button = view.findViewById(R.id.placeorder)
        val pname_input:TextInputEditText=view.findViewById(R.id.pname_input)
        val paddress_input:TextInputEditText=view.findViewById(R.id.paddress_input)
        val plandmark_input:TextInputEditText=view.findViewById(R.id.plandmark_input)
    }

}