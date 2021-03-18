package com.tripplleat.trippleattcustomer.ui.home.seller.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

/*
* This is an adapter which manage the viewpager i.e published and pending fragments
* */

class ViewPagerAdapter( fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager,lifecycle){

    val fragmentList = ArrayList<Fragment>()
    val fragmentListTitle = ArrayList<String>()

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }

    public fun addFragment(fragment: Fragment,title : String){
        fragmentList.add(fragment)
        fragmentListTitle.add(title)
    }

    public fun getTitle(position: Int): String{
        return fragmentListTitle[position]
    }

}