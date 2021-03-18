package com.tripplleat.trippleattcustomer.ui.home.customer

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tripplleat.trippleattcustomer.repo.CustomerRepo
import com.tripplleat.trippleattcustomer.ui.home.customer.viewmodel.CustomerViewModel


@Suppress("UNCHECKED_CAST")
class CustomerViewModelFactory (private val repository : CustomerRepo,private val application : Application) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CustomerViewModel(repository,application) as T
    }
}