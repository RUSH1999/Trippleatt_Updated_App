package com.tripplleat.trippleattcustomer

import android.app.Application
import com.tripplleat.trippleattcustomer.repo.CustomerRepo
import com.tripplleat.trippleattcustomer.repo.HomeRepo
import com.tripplleat.trippleattcustomer.ui.auth.customer.AuthViewModelFactory
import com.tripplleat.trippleattcustomer.repo.LoginRepo
import com.tripplleat.trippleattcustomer.ui.auth.viewmodel.AuthViewModal
import com.tripplleat.trippleattcustomer.ui.home.customer.CustomerViewModelFactory
import com.tripplleat.trippleattcustomer.ui.home.customer.viewmodel.CustomerViewModel
import com.tripplleat.trippleattcustomer.ui.home.seller.HomeViewModelFactory
import com.tripplleat.trippleattcustomer.ui.home.viewmodel.HomeViewModal
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class Myapplication : Application(), KodeinAware {
    override val kodein= Kodein.lazy {
        import(androidXModule(this@Myapplication))
        bind() from singleton { AuthViewModal(instance(),instance()) }
        bind() from singleton { LoginRepo() }
        bind() from provider{ AuthViewModelFactory(instance(),instance()) }

        bind() from singleton { HomeViewModal(instance(),instance()) }
        bind() from singleton { HomeRepo() }
        bind() from provider{
            HomeViewModelFactory(instance(), instance())
        }

        bind() from singleton { CustomerViewModel(instance(),instance()) }
        bind() from singleton { CustomerRepo() }
        bind() from provider {
            CustomerViewModelFactory(instance(),instance())
        }
    }
}