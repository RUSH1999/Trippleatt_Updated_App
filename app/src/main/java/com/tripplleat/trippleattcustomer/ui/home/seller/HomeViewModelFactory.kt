package com.tripplleat.trippleattcustomer.ui.home.seller

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tripplleat.trippleattcustomer.repo.HomeRepo
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory (private val repository : HomeRepo,private val application : Application) : ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModal(repository,application) as T
    }
}