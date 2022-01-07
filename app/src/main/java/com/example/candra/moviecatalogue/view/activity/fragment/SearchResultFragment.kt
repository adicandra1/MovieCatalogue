package com.example.candra.moviecatalogue.view.activity.fragment

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.candra.commonuicomponents.view.activity.fragment.BaseListFragment
import com.example.candra.commonuicomponents.view.exported_swipeRefresh
import com.example.candra.moviecatalogue.R
import com.example.candra.moviecatalogue.utils.isConnectedToInternet
import com.example.candra.moviecatalogue.view.activity.DetailActivity
import com.example.candra.moviecatalogue.view.activity.helper.InternetConnectionChecker
import com.example.candra.moviecatalogue.view.activity.helper.ViewOnConnectedListener
import com.example.candra.moviecatalogue.viewmodel.SearchResultViewModel
import com.example.candra.utils.Constant
import com.google.android.material.snackbar.Snackbar

class SearchResultFragment : BaseListFragment() {
    override var dataType: Int = -1
    override fun setIntentDetailActivity(): Intent = Intent(context, DetailActivity::class.java)

    private var query = ""
    private lateinit var searchResultViewModel: SearchResultViewModel

    private var isDataLoadedSuccessfully = false

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

        internetConnectionChecker =
            InternetConnectionChecker(ViewOnConnectedListener {
                if (!isDataLoadedSuccessfully) getData()
            })
    }

    override fun getFragmentAgrumentData() {
        super.getFragmentAgrumentData()
        arguments?.let {
            it.getString(ARG_SEARCH_QUERY)?.let { q -> query = q }
                ?: throw IllegalArgumentException("searchQuery null, it is either not supplied at fragment initialization, or any other cause")
        }
    }

    override fun initViewModel() {
        searchResultViewModel = ViewModelProviders.of(this)[SearchResultViewModel::class.java]
    }

    override fun setUpviewModelObserver() {
        searchResultViewModel.searchResult.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.setData(it)
                isDataLoadedSuccessfully = true
            } ?: showErrorMessage()
            exported_swipeRefresh.isRefreshing = false
        })
    }

    override fun getData() {
        val isConnected = isConnectedToInternet(context)
        if (isConnected) {
            exported_swipeRefresh.isRefreshing = true
            searchResultViewModel.loadSearchResult(dataType, query)
        } else {
            exported_swipeRefresh.isRefreshing = false

            this.view?.let {
                Snackbar.make(it, R.string.error_internet_disconnected, Snackbar.LENGTH_LONG)
                    .setAction(R.string.dismiss) {}
                    .show()
            }
        }
    }

    private fun showErrorMessage() {
        this.view?.let {
            Snackbar.make(it, R.string.data_failed_to_load, Snackbar.LENGTH_LONG)
                .setAction(R.string.retry) { getData() }
                .show()
        }
    }

    //companion object for fragment custom initialization
    companion object {
        private const val ARG_SEARCH_QUERY = "searchQuery"

        fun newInstance(dataType: Int, query: String): SearchResultFragment {
            val args = Bundle()
            args.putInt(ARG_DATA_TYPE, dataType)
            args.putString(ARG_SEARCH_QUERY, query)
            val fragment = SearchResultFragment()
            fragment.arguments = args
            return fragment
        }
    }
}