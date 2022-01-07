package com.example.candra.favoritemovie2

import com.example.candra.favoritefeature2.view.activity.fragment.FavoriteTabHolder
import com.example.candra.favoritefeature2.view.adapter.ViewPagerAdapter

class FavoriteTabHolderOutsideSource : FavoriteTabHolder() {
    override fun getViewPagerAdapter(): ViewPagerAdapter? {
        return context?.let { ViewPagerAdapterOutsideSource(it, childFragmentManager) }
    }
}