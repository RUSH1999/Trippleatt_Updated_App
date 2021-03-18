package com.tripplleat.trippleattcustomer.ui.home.seller.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.modal.Variant
import com.tripplleat.trippleattcustomer.modal.VariantLoose
import com.tripplleat.trippleattcustomer.modal.VariantSeller
import com.tripplleat.trippleattcustomer.modal.VariantSellerLoose
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal

/*
* This is a recyler adapter which handel the published product list, which have been aprooved by the data base administrator and it is ready
* to show to the user which can be ordered by them and managed by the selller
*  */

class PublishedRecyclerAdapter(
    val context: Context,
    var packedList: List<VariantSeller>, //list of packed product which contain the sellling price and stock of the certain product variant
    var looseList: List<VariantSellerLoose>,//list of loose product which contain the sellling price and stock of the certain product variant
    var size: Int,
    var wholePackedList: ArrayList<Variant>,//list of  all packed product variant present in database
    var wholeLooseList: ArrayList<VariantLoose>,//list of  all loose product variant present in database
    var stop: MutableLiveData<Boolean>,
    val viewModel: HomeViewModal //viewmodel of our activity
) : RecyclerView.Adapter<PublishedRecyclerAdapter.PackedProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackedProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.published_recycler_view, parent, false)
        return PackedProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return size
    }

    override fun onBindViewHolder(holder: PackedProductViewHolder, position: Int) {
        /*
        * checking the packed product list size and the total size of list which contain packed and loose list both
        * adn when the position exceed the packed product size then the loose product will be shown*/

        if (position < packedList.size) {
            val id = packedList[position].vairantId
            for (i in 0 until wholePackedList.size) {
                if (id.equals(wholePackedList[i].id, true)) {
                    holder.txtItemName.text = wholePackedList[i].toString()
                    holder.txtSi.text = (position + 1).toString()
                    holder.txtxCategory.text = wholePackedList[i].category
                    holder.txtStocks.text = packedList[position].Stock
                    holder.txtMrp.text = wholePackedList[i].mrp
                    holder.txtPrice.text = packedList[position].sellingPrice
                }
            }
        } else {
            val id = looseList[position - packedList.size].vairantId
            for (i in 0 until wholeLooseList.size) {
                if (id.equals(wholeLooseList[i].id, true)) {
                    holder.txtItemName.text = wholeLooseList[i].toString()
                    holder.txtSi.text = (position + 1).toString()
                    holder.txtxCategory.text = wholeLooseList[i].category
                    holder.txtStocks.text = looseList[position - packedList.size].Stock
                    holder.txtMrp.setText("Null")
                    holder.txtPrice.text = looseList[position - packedList.size].sellingPrice
                }
            }
        }
        if (position == (size - 1)) {
            stop.value = true
        }
        /*
        * Creating a Alert dialog to to give option to update the product
        * i.e through stock or selling price.
        * */
        holder.txtOptions.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Update Stock Or Price")
            val layout = LinearLayout(context)
            layout.orientation = LinearLayout.VERTICAL
            layout.setPadding(10, 10, 10, 10)
            val editTextPrice = EditText(context)
            editTextPrice.setHint("Enter the new Selling Price")
            layout.addView(editTextPrice)
            val editTextStock = EditText(context)
            editTextStock.setHint("Enter the new Stock")
            layout.addView(editTextStock)
            builder.setView(layout)

            /*
            * setting buttons in alert dialog to update or cancel the dialog
            * */

            builder.setPositiveButton("Update") { text, listener ->
                if (position < packedList.size) {
                    var productName: String? = null
                    val variantId = packedList[position].vairantId
                    for (i in 0 until wholePackedList.size) {
                        if (variantId.equals(wholePackedList[i].id, true)) {
                            productName = wholePackedList[i].productName
                        }
                    }
                    viewModel.updateMyVariantPacked(
                        editTextPrice.text.toString().trim(),
                        editTextStock.text.toString().trim(),
                        variantId,
                        productName!!
                    )
                } else {
                    var productName: String? = null
                    val variantId = looseList[position - packedList.size].vairantId
                    for (i in 0 until wholeLooseList.size) {
                        if (variantId.equals(wholeLooseList[i].id, true)) {
                            productName = wholeLooseList[i].productName
                        }
                    }
                    viewModel.updateMyVariantLoose(
                        editTextPrice.text.toString().trim(),
                        editTextStock.text.toString().trim(),
                        variantId,
                        productName!!
                    )
                }

            }
            builder.setNegativeButton("Cancel") { text, listener ->

            }
            builder.create()
            builder.show()
        }

    }

    class PackedProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtSi: TextView = view.findViewById(R.id.txtSiPublished)
        val txtItemName: TextView = view.findViewById(R.id.txtItemNamePublished)
        val txtxCategory: TextView = view.findViewById(R.id.txtCategoryPublished)
        val txtStocks: TextView = view.findViewById(R.id.txtStocksPublished)
        val txtMrp: TextView = view.findViewById(R.id.txtMrpPublished)
        val txtPrice: TextView = view.findViewById(R.id.txtPricePublished)
        val txtOptions: TextView = view.findViewById(R.id.txtOptions)
    }
}