package com.example.candra.favoritemovie2

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.example.candra.favoritefeature2.view.activity.fragment.FavoriteDigitalShowFragment
import com.example.candra.favoritefeature2.view.adapter.ViewPagerAdapter
import com.example.candra.utils.Constant.TYPE_MOVIE
import com.example.candra.utils.Constant.TYPE_TVSHOW

class ViewPagerAdapterOutsideSource(context: Context, fragmentManager: FragmentManager) :
    ViewPagerAdapter(context, fragmentManager) {
    override val pages: List<FavoriteDigitalShowFragment> = listOf(
        FavoriteFragmentOutsideSource.dataType(TYPE_MOVIE),
        FavoriteFragmentOutsideSource.dataType(TYPE_TVSHOW)
    )
}