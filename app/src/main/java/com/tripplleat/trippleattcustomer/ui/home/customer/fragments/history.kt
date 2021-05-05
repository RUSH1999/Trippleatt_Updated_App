package com.tripplleat.trippleattcustomer.ui.home.customer.fragments

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
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.ui.home.customer.CustomerViewModelFactory
import com.tripplleat.trippleattcustomer.ui.home.customer.adapters.CartRecylerAdapter
import com.tripplleat.trippleattcustomer.ui.home.customer.viewmodel.CustomerViewModel
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

// TODO: Rename parameter arguments, choose names that match

class history : Fragment(), KodeinAware {

    lateinit var recyclerViewCart : RecyclerView
    lateinit var viewModel : CustomerViewModel
    lateinit var layoutManager : LinearLayoutManager
    lateinit var bottom_sheet : BottomSheetBehavior<LinearLayout>
    lateinit var cartAdapter : CartRecylerAdapter
    lateinit var linearLayout: LinearLayout

    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: CustomerViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_history, container, false)
        viewModel = ViewModelProvider(requireActivity(),factory).get(CustomerViewModel::class.java)
        initViews(view)
        layoutManager = LinearLayoutManager(activity as Context)
        cartAdapter = CartRecylerAdapter(requireContext(),viewModel._ProductsInOrder.value!!,viewModel,bottom_sheet)
        recyclerViewCart.layoutManager = layoutManager
        recyclerViewCart.adapter = cartAdapter
        viewModel._ProductsInCart.observe(viewLifecycleOwner, Observer {
            cartAdapter.notifyDataSetChanged()
        })



        return view
    }

    private fun initViews(view: View) {
        recyclerViewCart = view?.findViewById(R.id.history)
        linearLayout = view.findViewById(R.id.bottomSheet)
        bottom_sheet = BottomSheetBehavior.from(linearLayout)

    }


}