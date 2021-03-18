package com.tripplleat.trippleattcustomer.ui.home.customer.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.ui.home.customer.CustomerViewModelFactory
import com.tripplleat.trippleattcustomer.ui.home.customer.adapters.StoresRecyclerAdapter
import com.tripplleat.trippleattcustomer.ui.home.customer.viewmodel.CustomerViewModel
import com.tripplleat.trippleattcustomer.util.showDialog
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

/*
* This is our home fragment which will be shown to the customer as customer make a login
* then this fragment will be opened
* */

class Home_fragment : Fragment(), KodeinAware {

    lateinit var viewModel : CustomerViewModel
    lateinit var layoutManager : LinearLayoutManager
    lateinit var storesAdapter : StoresRecyclerAdapter
    lateinit var storesRecyclerView : RecyclerView
    var pd : Dialog? = null
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: CustomerViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.shop_nearby,container,false)
        viewModel= ViewModelProvider(requireActivity(),factory).get(CustomerViewModel::class.java)

        if(viewModel.start == 0){
            pd = context?.showDialog("please wait")
            pd?.show()
            viewModel.start = 1
        }
        initViews(view)

        viewModel.storesList.observe(viewLifecycleOwner, Observer {
            layoutManager = LinearLayoutManager(activity as Context)
            storesAdapter = StoresRecyclerAdapter(requireContext(),viewModel,viewModel.storesList.value!!)
            storesRecyclerView.layoutManager = layoutManager
            storesRecyclerView.adapter = storesAdapter
            pd?.dismiss()
        })
        return view
    }

    private fun initViews(view: View) {
        storesRecyclerView = view.findViewById(R.id.storeRecyclerView)
    }
}