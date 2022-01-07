package com.example.candra.moviecatalogue.view.activity.fragment

import com.example.candra.favoritefeature2.view.activity.fragment.FavoriteTabHolder
import com.example.candra.favoritefeature2.view.adapter.ViewPagerAdapter
import com.example.candra.moviecatalogue.view.adapter.ViewPagerAdapterInsideSource

//to hold tab fragment
class FavoriteTabHolderInsideSource : FavoriteTabHolder() {

    override fun getViewPagerAdapter(): ViewPagerAdapter? {
        return context?.let { ViewPagerAdapterInsideSource(it, childFragmentManager) }
    }

}