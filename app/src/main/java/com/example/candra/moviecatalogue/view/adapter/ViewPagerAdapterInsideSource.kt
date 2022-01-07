package com.example.candra.moviecatalogue.view.adapter

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.example.candra.favoritefeature2.view.activity.fragment.FavoriteDigitalShowFragment
import com.example.candra.favoritefeature2.view.adapter.ViewPagerAdapter
import com.example.candra.moviecatalogue.view.activity.fragment.FavoriteDigitalShowFragmentInsideSource
import com.example.candra.utils.Constant.TYPE_MOVIE
import com.example.candra.utils.Constant.TYPE_TVSHOW

class ViewPagerAdapterInsideSource(context: Context, fm: FragmentManager) :
    ViewPagerAdapter(context, fm) {
    override val pages: List<FavoriteDigitalShowFragment> = listOf(
        FavoriteDigitalShowFragmentInsideSource.dataType(TYPE_MOVIE),
        FavoriteDigitalShowFragmentInsideSource.dataType(TYPE_TVSHOW)
    )
}