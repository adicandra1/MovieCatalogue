package com.example.candra.moviecatalogue.view.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.example.candra.moviecatalogue.R
import com.example.candra.moviecatalogue.view.activity.fragment.FavoriteTabHolderInsideSource
import com.example.candra.moviecatalogue.view.activity.fragment.ItemListFragment
import com.example.candra.moviecatalogue.view.activity.fragment.SearchResultFragment
import com.example.candra.utils.Constant
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ActivityState", "CREATED")
        setContentView(R.layout.activity_main)

        loadFragment(
            savedInstanceState,
            ItemListFragment.dataType(Constant.TYPE_MOVIE),
            Constant.MOVIE_FRAGMENT
        )

        bottomNavigationView?.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.movies -> {
                    loadFragment(
                        savedInstanceState,
                        ItemListFragment.dataType(Constant.TYPE_MOVIE),
                        Constant.MOVIE_FRAGMENT
                    )
                }
                R.id.tvShow -> {
                    loadFragment(
                        savedInstanceState,
                        ItemListFragment.dataType(Constant.TYPE_TVSHOW),
                        Constant.TVSHOW_FRAGMENT
                    )
                }
                R.id.favorite -> {
                    loadFragment(
                        savedInstanceState,
                        FavoriteTabHolderInsideSource(),
                        Constant.FAVORITE_TAB_FRAGMENT
                    )
                }
            }
            true
        } ?: Log.d("BottomNav", "Not Exist")

        handleSearchViewIntent(savedInstanceState, intent)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu?.findItem(R.id.searchView)?.actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.action_change_languange) {
            val changeLanguageIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
            startActivity(changeLanguageIntent)
        }
        if (item?.itemId == R.id.action_setting) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadFragment(savedInstanceState: Bundle?, fragment: Fragment, key: String) {
        if (savedInstanceState == null && supportFragmentManager.findFragmentByTag(key) == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_content, fragment, key)
                .commit()
        }
    }

    private fun handleSearchViewIntent(savedInstanceState: Bundle?, intent: Intent) {
        if (Intent.ACTION_SEARCH == intent.action) {
            val query = intent.getStringExtra(SearchManager.QUERY)
            val sharedPreferences =
                applicationContext.getSharedPreferences("dataType", Context.MODE_PRIVATE)
            val dataType = sharedPreferences.getInt(Constant.ITEM_TYPE, Constant.TYPE_MOVIE)
            loadFragment(savedInstanceState, SearchResultFragment.newInstance(dataType, query), "")
            when (dataType) {
                Constant.TYPE_MOVIE -> bottomNavigationView.menu.findItem(R.id.movies).isChecked =
                    true
                Constant.TYPE_TVSHOW -> bottomNavigationView.menu.findItem(R.id.tvShow).isChecked =
                    true
                else -> throw IllegalStateException("invalid type")
            }
        }
    }
}
