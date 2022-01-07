package com.example.candra.commonuicomponents.view.activity.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.candra.commonuicomponents.view.adapter.ItemListAdapter
import com.example.candra.data.model.DigitalShow
import com.example.candra.utils.Constant
import com.example.candra.commonuicomponents.view.exported_itemListRV
import com.example.candra.commonuicomponents.view.exported_swipeRefresh
import com.example.candra.data.model.DigitalShowDetails

abstract class BaseListFragment : Fragment() {

    //variable declarations
    private lateinit var intentDetailActivity: Intent
    protected lateinit var adapter: ItemListAdapter
    abstract var dataType: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        getFragmentAgrumentData()

        initViewModel()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(com.example.candra.commonuicomponents.R.layout.item_list_fragment_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getFragmentAgrumentData()

        //setting up all adapter and layout manager
        adapter =
            ItemListAdapter(Glide.with(this)) { item: Any -> onRVItemClicked(dataType, item) }
        exported_itemListRV.adapter = adapter
        exported_itemListRV.layoutManager = LinearLayoutManager(context)

        setUpviewModelObserver()

        //registering listeners
        exported_swipeRefresh.setOnRefreshListener {
            getData()
        }


        //Initial load data
        if (!Constant.IS_INITIAL_DATA_LOAD_DONE) {
            getData()
            Constant.IS_INITIAL_DATA_LOAD_DONE = true
        }
    }

    open fun onRVItemClicked(dataType: Int, item: Any) {
        val id = when (item) {
            is DigitalShow -> item.id
            is DigitalShowDetails -> item.id
            else -> throw IllegalArgumentException("Wrong type!")
        }
        intentDetailActivity = setIntentDetailActivity()
        intentDetailActivity.putExtra(Constant.ITEM_ID, id)
        when (dataType) {
            Constant.TYPE_MOVIE -> intentDetailActivity.putExtra(
                Constant.ITEM_TYPE,
                Constant.TYPE_MOVIE
            )
            Constant.TYPE_TVSHOW -> intentDetailActivity.putExtra(
                Constant.ITEM_TYPE,
                Constant.TYPE_TVSHOW
            )
        }
        startActivity(intentDetailActivity)
    }

    open fun getFragmentAgrumentData() {
        arguments?.let {
            dataType = it.getInt(ARG_DATA_TYPE)
        }
    }

    open fun setIntentDetailActivity() : Intent {
        return Intent("com.example.candra.moviecatalogue.detailactivity.open")
    }

    abstract fun initViewModel()

    abstract fun setUpviewModelObserver()

    abstract fun getData()


    //companion object for fragment custom initialization
    companion object {
        const val ARG_DATA_TYPE = "dataType"
    }
}