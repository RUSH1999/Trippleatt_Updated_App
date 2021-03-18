package com.tripplleat.trippleattcustomer.ui.home.customer.activity

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tripplleat.trippleattcustomer.R
import com.tripplleat.trippleattcustomer.databinding.FragmentCustProfileBinding
import com.tripplleat.trippleattcustomer.ui.home.customer.CustomerViewModelFactory
import com.tripplleat.trippleattcustomer.ui.home.customer.adapters.StoresRecyclerAdapter
import com.tripplleat.trippleattcustomer.ui.home.customer.viewmodel.CustomerViewModel
import com.tripplleat.trippleattcustomer.util.showDialog
import kotlinx.android.synthetic.main.fragment_cust_profile.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class profileFragment : Fragment(), KodeinAware {

    lateinit var viewModel : CustomerViewModel
    override val kodein: Kodein by kodein { activity?.applicationContext!! }
    private val factory: CustomerViewModelFactory by instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding:FragmentCustProfileBinding= DataBindingUtil.inflate(inflater, R.layout.fragment_cust_profile,container,false)
        viewModel= ViewModelProvider(requireActivity(), factory).get(CustomerViewModel::class.java)
        binding.viewmodel=viewModel
        viewModel.fetchCustomerProfile()
        viewModel.customerDetail.observe(viewLifecycleOwner, Observer {
        name.setText(it.first_name+" "+it.last_name)
        mobile.setText(it.mobile_number)
})

        return binding.root
    }


}