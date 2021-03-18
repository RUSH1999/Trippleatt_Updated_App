package com.tripplleat.trippleattcustomer.ui.home.seller

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.FragmentInitialAddProductfragmentBinding
import com.tripplleat.trippleattcustomer.ui.home.seller.adapter.PublishedRecyclerAdapter
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal
import com.tripplleat.trippleattcustomer.util.showDialog

/**
 * published fragment which will contain the published product detail
 * Note : As now we  have worked on intial phase so by default we have make all product as published,
 * so there is nothing like pending now but there will be.
 */
class PublishedFragment : Fragment() {

    lateinit var viewModel : HomeViewModal
    var stop = MutableLiveData<Boolean>()
    lateinit var layoutManager : LinearLayoutManager
    lateinit var publishedAdapter : PublishedRecyclerAdapter
    lateinit var publishRecyclerView : RecyclerView
    var pd : Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_published,container,false)
        viewModel =  ViewModelProvider(requireActivity()).get(HomeViewModal::class.java)
        stop.value = false
        if(viewModel.start == 0){
            pd = context?.showDialog("please wait")
            pd?.show()
            viewModel.start = 1
        }

        initViews(view)

        viewModel._publishedLoose.observe(viewLifecycleOwner, Observer {
            if(viewModel._publishedPacked.value!!.size == 0 && viewModel._publishedLoose.value!!.size == 0)
                pd?.dismiss()

            layoutManager = LinearLayoutManager(activity as Context)
            val size = viewModel._publishedPacked.value!!.size + viewModel._publishedLoose.value!!.size
            Log.i("product_size",size.toString())
            publishedAdapter = PublishedRecyclerAdapter(requireContext(),viewModel._publishedPacked.value!!,viewModel._publishedLoose.value!!,size,viewModel.variantsList,viewModel.variantsListLoose,stop,viewModel)
            publishRecyclerView.layoutManager = layoutManager
            publishRecyclerView.adapter = publishedAdapter
        })

        stop.observe(viewLifecycleOwner, Observer {
            if(it == true){
                pd?.dismiss()
                stop.value = false
            }
        })

        return view
    }

    private fun initViews(root: View) {
        publishRecyclerView = root.findViewById(R.id.publishedRecyclerView)
    }

}
