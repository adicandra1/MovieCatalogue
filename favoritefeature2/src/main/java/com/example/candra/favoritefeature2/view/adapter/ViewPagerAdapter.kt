package com.example.candra.favoritefeature2.view.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.candra.favoritefeature2.R
import com.example.candra.favoritefeature2.view.activity.fragment.FavoriteDigitalShowFragment

abstract class ViewPagerAdapter(private val context: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    abstract val pages : List<FavoriteDigitalShowFragment>

    override fun getCount(): Int {
        return pages.size
    }

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.resources.getString(R.string.movies)
            else -> context.resources.getString(R.string.tv_show)
        }
    }
}