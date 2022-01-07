package com.example.candra.favoritemovie2

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.example.candra.favoritefeature2.view.activity.fragment.FavoriteDigitalShowFragment

class FavoriteFragmentOutsideSource : FavoriteDigitalShowFragment() {

    //companion object for fragment custom initialization
    companion object {
        private const val ARG_DATA_TYPE = "dataType"

        fun dataType(dataType: Int): FavoriteFragmentOutsideSource {
            val args = Bundle()
            args.putInt(ARG_DATA_TYPE, dataType)
            val fragment = FavoriteFragmentOutsideSource()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initViewModel() {
        favoriteDigitalShowViewModel =
            ViewModelProviders.of(this)[FavoriteDigitalShowViewModelOutsideSource::class.java]
    }
}