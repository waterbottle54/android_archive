package com.holy.interiortalk.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.holy.interiortalk.FurnitureFragment
import com.holy.interiortalk.PictureFragment
import com.holy.interiortalk.PostFragment

class WritePagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PictureFragment()
            1 -> FurnitureFragment()
            2 -> PostFragment()
            else -> PictureFragment()
        }
    }
}