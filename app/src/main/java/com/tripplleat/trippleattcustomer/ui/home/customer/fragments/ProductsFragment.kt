package com.tripplleat.trippleattcustomer.ui.home.customer.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.FragmentStoreProductsDetailsBinding
import com.tripplleat.trippleattcustomer.ui.home.customer.CustomerViewModelFactory
import com.tripplleat.trippleattcustomer.ui.home.customer.adapters.ProductRecyclerAdapter
import com.tripplleat.trippleattcustomer.ui.home.customer.viewmodel.CustomerViewModel
import com.tripplleat.trippleattcustomer.util.showDialog
import com.tripplleat.trippleattcustomer.util.toast
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

/*
* This is a product fragment which actually use th productrecyclerView and maintain all the product functionality
*
 */

class ProductsFragment : Fragment(), KodeinAware {

    lateinit var linearLayout: LinearLayout
    lateinit var bottom_sheet: BottomSheetBehavior<LinearLayout>
    lateinit var viewModel: CustomerViewModel
    lateinit var layoutManager: LinearLayoutManager
    lateinit var productAdapter: ProductRecyclerAdapter
    lateinit var productRecyclerView: RecyclerView
    var image = MutableLiveData<String>()
    var price = MutableLiveData<String>()
    var pd: Dialog? = null
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: CustomerViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentStoreProductsDetailsBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_store_products_details,
            container,
            false
        )
        viewModel = ViewModelProvider(requireActivity(), factory).get(CustomerViewModel::class.java)
        initViews(binding.root)
        viewModel.fetchProductList()
        pd = context?.showDialog("please wait")
        pd?.show()

        viewModel._productList.observe(viewLifecycleOwner, Observer {
            viewModel.fetchPackedAndLooseVariant()

        })

        viewModel._packedProductMain.observe(viewLifecycleOwner, Observer {
            layoutManager = LinearLayoutManager(activity as Context)
            productAdapter = ProductRecyclerAdapter(
                requireContext(),
                viewModel._productList.value!!,
                viewModel._PackedProductSeller.value!!,
                viewModel._packedProductMain.value!!,
                viewModel._looseProductSeller.value!!,
                viewModel._looseProductMain.value!!,
                viewModel.currentVariant,
                viewModel.currentCartList,
                bottom_sheet,
                viewModel
            )
            productRecyclerView.layoutManager = layoutManager
            productRecyclerView.adapter = productAdapter
            pd?.dismiss()
        })

        viewModel.isProductAddedToCart.observe(viewLifecycleOwner, Observer {
            if (it == 1) {
                requireContext().toast("Product added to cart sucessfully")
                viewModel.isProductAddedToCart.value = 0
            } else if (it == -1) {
                requireContext().toast("some error ocurred")
                viewModel.isProductAddedToCart.value = 0
            }

        })

        return binding.root
    }

    private fun initViews(root: View) {
        productRecyclerView = root.findViewById(R.id.productRecylerViewCustomer)
        linearLayout = root.findViewById(R.id.bottomSheet)
        bottom_sheet = BottomSheetBehavior.from(linearLayout)
    }
}