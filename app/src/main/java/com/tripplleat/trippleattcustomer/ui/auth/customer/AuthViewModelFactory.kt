package com.tripplleat.trippleattcustomer.ui.auth.customer

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tripplleat.trippleattcustomer.repo.LoginRepo
import com.tripplleat.trippleattcustomer.ui.auth.viewmodel.AuthViewModal

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory (private val userepository: LoginRepo,private val application: Application):ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModal(userepository,application) as T
    }
}