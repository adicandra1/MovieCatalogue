package com.example.candra.moviecatalogue.view.activity.fragment

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.candra.favoritefeature2.view.activity.fragment.FavoriteDigitalShowFragment
import com.example.candra.moviecatalogue.view.activity.DetailActivity
import com.example.candra.moviecatalogue.viewmodel.FavoriteDigitalShowViewModelInsideSource

class FavoriteDigitalShowFragmentInsideSource : FavoriteDigitalShowFragment() {

    override fun setIntentDetailActivity(): Intent = Intent(context, DetailActivity::class.java)

    override fun initViewModel() {
        favoriteDigitalShowViewModel =
            ViewModelProviders.of(this)[FavoriteDigitalShowViewModelInsideSource::class.java]
    }

    //companion object for fragment custom initialization
    companion object {
        fun dataType(dataType: Int): FavoriteDigitalShowFragmentInsideSource {
            val args = Bundle()
            args.putInt(ARG_DATA_TYPE, dataType)
            val fragment = FavoriteDigitalShowFragmentInsideSource()
            fragment.arguments = args
            return fragment
        }
    }

}