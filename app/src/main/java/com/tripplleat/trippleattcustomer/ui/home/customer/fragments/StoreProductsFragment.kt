package com.tripplleat.trippleattcustomer.ui.home.customer.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.ui.home.customer.CustomerViewModelFactory
import com.tripplleat.trippleattcustomer.ui.home.customer.modal.ProductDetails
import com.tripplleat.trippleattcustomer.ui.home.customer.viewmodel.CustomerViewModel
import com.tripplleat.trippleattcustomer.ui.home.seller.adapter.ViewPagerAdapter
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
/*
* This is a main fragment which contains 3 fragments product, about us and review
* */

class StoreProductsFragment  : Fragment(), KodeinAware {

    lateinit var viewPager2: ViewPager2
    lateinit var tabLayout: TabLayout

    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: CustomerViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.shop_products,container,false)
        val viewModel = ViewModelProvider(requireActivity(),factory).get(CustomerViewModel::class.java)
        viewPager2 = view.findViewById(R.id.customerProductViewPager)
        tabLayout = view.findViewById(R.id.customerTabLayout)
        setPageAdapter()

        val callBack : OnBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                view?.findNavController()?.navigate(R.id.action_storeProductsFragment_to_home_fragment)
                viewModel.currentVariant.clear()
                viewModel.currentVariant.put(-1,
                    ProductDetails("fake","fake","fake",-1,-1,-1,-1,"fake",0,"fake","fake","fake","fake","fake")
                )
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callBack)

        return view
    }

    private fun setPageAdapter(){
        val adapter = ViewPagerAdapter(childFragmentManager,lifecycle)
        adapter.addFragment(ProductsFragment(),"Products")
        adapter.addFragment(AboutUsFragment(),"About us")
        adapter.addFragment(ReviewFragment(),"Review")
        viewPager2.adapter = adapter
        TabLayoutMediator(tabLayout,viewPager2){tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()
    }
}