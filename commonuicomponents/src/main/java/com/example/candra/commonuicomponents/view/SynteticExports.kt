package com.example.candra.commonuicomponents.view

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.item_list_fragment_layout.*
import kotlinx.android.synthetic.main.favorite_tab_layout.*

inline val Fragment.exported_itemListRV: RecyclerView get() = itemListRV

inline val Fragment.exported_swipeRefresh: SwipeRefreshLayout get() = swipeRefresh

inline val Fragment.exported_viewPagerMain: ViewPager get() = viewPagerMain

inline val Fragment.exported_tabLayout: TabLayout get() = tabLayout