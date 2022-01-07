package com.example.candra.moviecatalogue.view.activity.fragment

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.candra.commonuicomponents.view.activity.fragment.BaseListFragment
import com.example.candra.commonuicomponents.view.exported_swipeRefresh
import com.example.candra.moviecatalogue.R
import com.example.candra.moviecatalogue.utils.isConnectedToInternet
import com.example.candra.moviecatalogue.view.activity.DetailActivity
import com.example.candra.moviecatalogue.view.activity.helper.InternetConnectionChecker
import com.example.candra.moviecatalogue.view.activity.helper.ViewOnConnectedListener
import com.example.candra.moviecatalogue.viewmodel.ItemListViewModel
import com.example.candra.utils.Constant
import com.example.candra.utils.Constant.TYPE_MOVIE
import com.example.candra.utils.Constant.TYPE_TVSHOW
import com.google.android.material.snackbar.Snackbar

class ItemListFragment : BaseListFragment() {

    //variable declarations
    override var dataType: Int = -1
    private lateinit var itemListViewModel: ItemListViewModel
    private var isMovie: Boolean = true
    private var isDataLoadedSuccessfully = false

    override fun setIntentDetailActivity(): Intent = Intent(context, DetailActivity::class.java)

    private lateinit var internetConnectionChecker: InternetConnectionChecker
    private val intentFilterInternetConnection = IntentFilter()

    init {
        intentFilterInternetConnection.addAction(Constant.CONNECTION_CHANGE)
    }

    override fun onResume() {
        super.onResume()
        context?.registerReceiver(internetConnectionChecker, intentFilterInternetConnection)
    }

    override fun onPause() {
        super.onPause()
        context?.unregisterReceiver(internetConnectionChecker)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = context?.getSharedPreferences("dataType", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putInt(Constant.ITEM_TYPE, dataType)
        editor?.apply()

        internetConnectionChecker =
            InternetConnectionChecker(ViewOnConnectedListener {
                if (!isDataLoadedSuccessfully) getData()
            })
    }

    private fun showErrorMessage() {
        this.view?.let {
            Snackbar.make(it, R.string.data_failed_to_load, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry) { getData() }
                .show()
        }
    }

    override fun getData() {
        val isConnected = isConnectedToInternet(context)
        if (isConnected) {
            exported_swipeRefresh.isRefreshing = true

            @Suppress("DEPRECATION") val locale =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales[0].toString() else resources.configuration.locale.toString()
            Log.d("Itrtytfyg", locale)

            itemListViewModel.loadDigitalShows(isMovie, locale)
        } else {
            exported_swipeRefresh.isRefreshing = false

            this.view?.let {
                Snackbar.make(it, R.string.error_internet_disconnected, Snackbar.LENGTH_LONG)
                    .setAction(R.string.dismiss) {}
                    .show()
            }
        }
    }

    override fun getFragmentAgrumentData() {
        super.getFragmentAgrumentData()

        //init variables
        isMovie = when (dataType) {
            TYPE_MOVIE -> true
            TYPE_TVSHOW -> false
            else -> throw IllegalArgumentException("incorrect data type")
        }
    }

    override fun initViewModel() {
        itemListViewModel = ViewModelProviders.of(this)[ItemListViewModel::class.java]
    }

    override fun setUpviewModelObserver() {
        itemListViewModel.getDigitalShows().observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.setData(it)
                isDataLoadedSuccessfully = true
            } ?: showErrorMessage()
            exported_swipeRefresh.isRefreshing = false
        })
    }

    //companion object for fragment custom initialization
    companion object {

        fun dataType(dataType: Int): ItemListFragment {
            return newInstance(dataType)
        }

        private fun newInstance(dataType: Int): ItemListFragment {
            val args = Bundle()
            args.putInt(ARG_DATA_TYPE, dataType)
            val fragment = ItemListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}