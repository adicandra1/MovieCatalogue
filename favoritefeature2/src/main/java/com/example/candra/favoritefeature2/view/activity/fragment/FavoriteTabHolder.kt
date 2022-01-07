package com.example.candra.favoritefeature2.view.activity.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.candra.commonuicomponents.view.exported_tabLayout
import com.example.candra.commonuicomponents.view.exported_viewPagerMain
import com.example.candra.favoritefeature2.R
import com.example.candra.favoritefeature2.view.adapter.ViewPagerAdapter

//to hold tab fragment
abstract class FavoriteTabHolder : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.favorite_tab_layout, container, false)
    }

    abstract fun getViewPagerAdapter() : ViewPagerAdapter?

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exported_viewPagerMain.adapter = getViewPagerAdapter()
        exported_tabLayout.setupWithViewPager(exported_viewPagerMain)
    }
}