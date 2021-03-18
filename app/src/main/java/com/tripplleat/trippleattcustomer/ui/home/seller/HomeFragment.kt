package com.tripplleat.trippleattcustomer.ui.home.seller

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.FragmentHomeBinding
import com.tripplleat.trippleattcustomer.ui.home.seller.adapter.ViewPagerAdapter
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal
import kotlinx.android.synthetic.main.fragment_home.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

/*
* This is Home fragment which wil open when a seller do login,
* this fragment contain the pending and published fragment as viewPager
*/

class HomeFragment : Fragment(), KodeinAware{

    lateinit var viewPager2: ViewPager2
    lateinit var tabLayout: TabLayout

    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: HomeViewModelFactory by instance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding : FragmentHomeBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_home,container,false)
        val viewModel = ViewModelProvider(requireActivity(),factory).get(HomeViewModal::class.java)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        initViews(binding.root)
        setPageAdapter()

        viewModel._productName.observe(viewLifecycleOwner, Observer {
            if(it != null)
                Log.i("product","${it}")
        })

        viewModel._variants.observe(viewLifecycleOwner, Observer {
            if(it != null){
                Log.i("product","${it}")
                //viewModel.addProductAndvariant()
            }
        })

        viewModel._variantsLoose.observe(viewLifecycleOwner, Observer {
            if(it != null){
                Log.i("product","${it}")
                viewModel.addProductAndvariant()
            }
        })

        val addProduct : LiveData<Boolean> = viewModel.addProduct
        addProduct.observe(viewLifecycleOwner, Observer {
            if(it == true){
                requireView().findNavController().navigate(R.id.action_homeFragment_to_initialAddProductfragment)
                viewModel.addProduct.value = false
            }

        })

        val callBack : OnBackPressedCallback = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }

        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callBack)

        return binding.root
    }

    private fun initViews(view: View){
        viewPager2 = view.findViewById(R.id.homeViewPager)
        tabLayout = view.findViewById(R.id.homeTabLayout)
    }

    private fun setPageAdapter(){
        val adapter = ViewPagerAdapter(childFragmentManager,lifecycle)
        adapter.addFragment(PublishedFragment(),"Published")
        adapter.addFragment(PendingFragment(),"Pending")
       viewPager2.adapter = adapter
        TabLayoutMediator(tabLayout,viewPager2){tab, position ->
            tab.text = adapter.getTitle(position)
        }.attach()
    }


}
