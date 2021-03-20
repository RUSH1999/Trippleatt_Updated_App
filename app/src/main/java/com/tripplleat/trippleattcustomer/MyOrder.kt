package com.tripplleat.trippleattcustomer

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tripplleat.trippleattcustomer.ui.home.customer.CustomerViewModelFactory
import com.tripplleat.trippleattcustomer.ui.home.customer.adapters.CartRecylerAdapter
import com.tripplleat.trippleattcustomer.ui.home.customer.viewmodel.CustomerViewModel
import com.tripplleat.trippleattcustomer.util.toast
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class MyOrder : Fragment(), KodeinAware {

    lateinit var viewModel : CustomerViewModel
    lateinit var recyclerViewCart : RecyclerView
    lateinit var layoutManager : LinearLayoutManager
    lateinit var bottom_sheet : BottomSheetBehavior<LinearLayout>
    lateinit var linearLayout: LinearLayout
    lateinit var cartAdapter : CartRecylerAdapter
    var pd : Dialog? = null

    override val kodein: Kodein by kodein { activity?.applicationContext!! }

    private val factory: CustomerViewModelFactory by instance()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_my_order,container,false)
        viewModel = ViewModelProvider(requireActivity(),factory).get(CustomerViewModel::class.java)
        initViews(view)
        layoutManager = LinearLayoutManager(activity as Context)
        cartAdapter = CartRecylerAdapter(requireContext(),viewModel._ProductsInCart.value!!,viewModel,bottom_sheet)
        recyclerViewCart.layoutManager = layoutManager
        recyclerViewCart.adapter = cartAdapter
        viewModel._ProductsInCart.observe(viewLifecycleOwner, Observer {
            cartAdapter.notifyDataSetChanged()
        })

        viewModel.isChangesDone.observe(viewLifecycleOwner, Observer {
            if(it == 1){
                context?.toast("Removed sucessfully")
                viewModel.isChangesDone.value = 0
            }
            if(it == 2){
                context?.toast("Some error ocurred")
                viewModel.isChangesDone.value = 0
            }
            if(it == 3){
                context?.toast("Product Updated sucessfully")
                viewModel.isChangesDone.value = 0
                val temp = CartRecylerAdapter(requireContext(),viewModel._ProductsInCart.value!!,viewModel,bottom_sheet).productAdded()
            }
            if(it == 4){
                context?.toast("Exceding the current stock please enter a less quantity")
                viewModel.isChangesDone.value = 0
            }
        })

        return view
    }

    private fun initViews(view: View) {
        recyclerViewCart = view?.findViewById(R.id.cartProductsRecyclerview)
        linearLayout = view.findViewById(R.id.bottomSheet)
        bottom_sheet = BottomSheetBehavior.from(linearLayout)
    }

}