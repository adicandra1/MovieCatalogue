package com.example.candra.favoritefeature2.view.activity.fragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.example.candra.commonuicomponents.view.activity.fragment.BaseListFragment
import com.example.candra.commonuicomponents.view.exported_swipeRefresh
import com.example.candra.favoritefeature2.viewmodel.FavoriteDigitalShowViewModel

abstract class FavoriteDigitalShowFragment : BaseListFragment() {

    private var isInitialDataLoadDone = false

    //variable declaration
    override var dataType = -1
    protected lateinit var favoriteDigitalShowViewModel: FavoriteDigitalShowViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isInitialDataLoadDone) {
            getData()
            isInitialDataLoadDone = true
        }
    }

    override fun setUpviewModelObserver() {
        favoriteDigitalShowViewModel.getFavoriteDigitalShow().observe(viewLifecycleOwner, Observer {
            adapter.setData(it.toMutableList())
            exported_swipeRefresh.isRefreshing = false
        })
    }

    override fun getData() {
        context?.let { favoriteDigitalShowViewModel.loadData(it, dataType) }
    }


}