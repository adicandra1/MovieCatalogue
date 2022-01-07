package com.example.candra.moviecatalogue.view.activity

import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.candra.commonuicomponents.view.adapter.ItemListAdapter
import com.example.candra.moviecatalogue.R
import com.example.candra.data.api.*
import com.example.candra.data.model.*
import com.example.candra.moviecatalogue.utils.*
import com.example.candra.moviecatalogue.view.activity.helper.*
import com.example.candra.moviecatalogue.viewmodel.DetailItemViewModel
import com.example.candra.utils.Constant.CONNECTION_CHANGE
import com.example.candra.utils.Constant.ITEM_ID
import com.example.candra.utils.Constant.ITEM_TYPE
import com.example.candra.utils.Constant.LOADED_DATA_CAST
import com.example.candra.utils.Constant.LOADED_DATA_KEYWORD
import com.example.candra.utils.Constant.LOADED_DATA_MOVIE_TV_SHOW
import com.example.candra.utils.Constant.LOAD_DATA_INITIAL_LOAD
import com.example.candra.utils.Constant.LOAD_DATA_REFRESH
import com.example.candra.utils.Constant.LOAD_DATA_REFRESH_DATABASE
import com.example.candra.utils.Constant.POSTER_IMAGE_SIZE
import com.example.candra.utils.Constant.TYPE_MOVIE
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_detail.*
import java.util.*


class DetailActivity : AppCompatActivity(), LocaleChangedListener.Companion.ViewListener {

    //When LocaleChanged
    override fun loadData() {
        showSnackbar(R.string.locale_changed)
        swipeRefresh.isRefreshing = true
        loadData(itemType, itemId, LOAD_DATA_INITIAL_LOAD)
    }

    private lateinit var castAdapter: ItemListAdapter
    private lateinit var detailItemViewModel: DetailItemViewModel
    private lateinit var snackbarActionRetryLoadDataListener: View.OnClickListener
    private lateinit var removeFromFavoriteAlertDialog: AlertDialog.Builder
    private var favoriteDigitalShow = FavoriteInsertData()

    /*
     * Data State
     */
    private var isFavorite = false
    private val loadedData = DataLoadedHolder()
    private var isMovie: Boolean = true
    private var itemId: String = ""
    private var itemType: Int = 0

    //used in internetConnectionChecker:ViewOnConnectedListener
    //used in snackbarActionRetryLoadDataListener
    // value set in loadFromApi function
    private var reloadDataStrategy = 0

    private var menuItem: Menu? = null

    private lateinit var internetConnectionChecker: InternetConnectionChecker
    private val intentFilterConnected = IntentFilter()

    init {
        intentFilterConnected.addAction(CONNECTION_CHANGE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        LocaleChangedListener.listener = this

        loadedData.resetLoadDataSuccessState()
        itemId = ""

        itemId = intent.getStringExtra(ITEM_ID)
        itemType = intent.getIntExtra(ITEM_TYPE, 0)
        isMovie = itemType == TYPE_MOVIE


        //init variables
        castAdapter = ItemListAdapter(Glide.with(this))
        detailItemViewModel = ViewModelProviders.of(this)[DetailItemViewModel::class.java]


        //view adapter and layout manager initializations
        castHolder.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        castHolder.adapter = castAdapter


        /*
         * registering all listener
         */
        registerObserver(itemType) //local fuction call
        swipeRefresh.setOnRefreshListener {
            loadData(itemType, itemId, LOAD_DATA_REFRESH)
        }

        internetConnectionChecker =
            InternetConnectionChecker(ViewOnConnectedListener {
                showSnackbar(R.string.we_online, R.string.load_data) {
                    if (!isLoadedSuccessfully())
                        swipeRefresh.isRefreshing = true
                    loadData(
                        itemType,
                        itemId,
                        reloadDataStrategy
                    )
                }

                applicationContext?.unregisterReceiver(internetConnectionChecker)

            })

        //when error loading data from api
        snackbarActionRetryLoadDataListener = View.OnClickListener {
            loadData(itemType, itemId, reloadDataStrategy)
        }


        /*
         * UI component initializations
         */
        removeFromFavoriteAlertDialog = AlertDialog.Builder(this)
            .setTitle(R.string.remove_fav_alert_dialog_title)
            .setMessage(R.string.remove_fav_alert_dialog_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                detailItemViewModel.removeFromFavorite(itemType, itemId)
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
    }


    /*
     * UI RELATED FUNCTIONS
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)
        menuItem = menu
        setFavoriteIcon(isFavorite)
        return super.onCreateOptionsMenu(menu)
    }

    private fun setFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) menuItem?.findItem(R.id.action_add_to_favorite)?.icon =
            getIcon(R.drawable.ic_favorite_added_24dp)
        else menuItem?.findItem(R.id.action_add_to_favorite)?.icon =
            getIcon(R.drawable.ic_favorite_24dp)
    }

    private fun getIcon(resId: Int): Drawable? {
        return ContextCompat.getDrawable(this, resId)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.action_change_languange -> {
                val changeLanguageIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(changeLanguageIntent)
                return true
            }
            R.id.action_setting -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.action_add_to_favorite -> {
                if (isFavorite) { //the data exist, we want to remove it, so:
                    removeFromFavoriteAlertDialog.show()
                } else {
                    if (isLoadedSuccessfully()) {
                        Log.d("addFav", "REACHED")
                        detailItemViewModel.addToFavorite(
                            this,
                            favoriteDigitalShow,
                            getLocale().toString(),
                            itemType,
                            itemId
                        )
                    } else {
                        showSnackbar(R.string.data_not_ready_warning)
                    }
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @Suppress("SameParameterValue")
    private fun showSnackbar(
        messageResId: Int,
        actionTextResId: Int,
        action: View.OnClickListener
    ) {
        Snackbar.make(swipeRefresh, messageResId, Snackbar.LENGTH_LONG)
            .setAction(actionTextResId, action)
            .show()
    }

    private fun showSnackbar(
        messageResId: Int,
        actionTextResId: Int = R.string.dismiss,
        action: (View) -> Unit = {}
    ) {
        Snackbar.make(swipeRefresh, messageResId, Snackbar.LENGTH_LONG)
            .setAction(actionTextResId, action)
            .show()
    }

    private fun showErrorMessage(
        source: String,
        msgRes: Int = R.string.data_failed_to_load,
        action: View.OnClickListener = snackbarActionRetryLoadDataListener
    ) {
        Log.d("errorLoad", source)
        showSnackbar(
            msgRes,
            R.string.retry,
            action
        )
        swipeRefresh.isRefreshing = false
    }

    private fun bindItem(item: DigitalShowDetails) {
        movieTitle.text = item.title
        releasedDateField.text = item.releasedDate?.formatToLocalDate(getLocale())
        overviewField.text = item.overview?.printNoticeIfNull(applicationContext)
        genreHolder.removeAllViews()
        item.genres?.forEach {
            genreHolder.addChip(it.name)
        }

        Glide.with(this)
            .load(item.posterPath?.let { TheMovieDBApi.getPosterImage(POSTER_IMAGE_SIZE, it) })
            .into(moviePosterField)

        when (item) {
            is MovieDetails -> {
                budgetField.text = item.budget?.formatToCurrency(getLocale())
                revenueField.text = item.revenue?.formatToCurrency(getLocale())
                runtimeField.text = item.runtime?.formatToHourMinutes(applicationContext)
                statusText.visibility = View.GONE
                statusField.visibility = View.GONE
            }
            is TvShowDetails -> {
                budgetText.text = resources.getString(R.string.type)
                revenueText.text = resources.getString(R.string.original_language)
                runtimeField.text = item.runtime?.printAll()
                budgetField.text = item.type
                revenueField.text = item.originalLanguage
                statusField.text = item.status
            }
            is FavoriteMovie -> {
                budgetField.text = item.budget?.formatToCurrency(getLocale())
                revenueField.text = item.revenue?.formatToCurrency(getLocale())
                runtimeField.text = item.runtime?.formatToHourMinutes(applicationContext)
                statusText.visibility = View.GONE
                statusField.visibility = View.GONE
            }
            is FavoriteTvShow -> {
                budgetText.text = resources.getString(R.string.type)
                revenueText.text = resources.getString(R.string.original_language)
                runtimeField.text = item.runtime?.printAll()
                budgetField.text = item.type
                revenueField.text = item.originalLanguage
                statusField.text = item.status
            }
        }
    }


    /*
     * DATA RELATED FUNCTIONS
     */

    private fun isLoadedSuccessfully() = favoriteDigitalShow.digitalShowDetails != null &&
            favoriteDigitalShow.casts != null &&
            favoriteDigitalShow.keywords != null


    private fun registerObserver(dataType: Int) {
        detailItemViewModel.dataType.value = dataType

        detailItemViewModel.getDigitalShowDetailsFromApi().observe(this, Observer {
            when (it) {
                is DataMovieDetails -> it.movieDetails?.let { movie ->
                    bindItem(movie)

                    favoriteDigitalShow.digitalShowDetails = movie

                } ?: showErrorMessage("movie")
                is DataTvShowDetails -> it.tvShowDetails?.let { tvShow ->
                    bindItem(tvShow)

                    favoriteDigitalShow.digitalShowDetails = tvShow


                } ?: showErrorMessage("tvShow")
                is DataCasts -> it.casts?.let { casts ->
                    castAdapter.setData(casts)
                    favoriteDigitalShow.casts = casts


                } ?: showErrorMessage("cast")
                is DataKeywords -> it.keywords?.let { keywords ->
                    keywordsHolder.removeAllViews()
                    keywords.forEach { keyword -> keywordsHolder.addChip(keyword.name) }

                    favoriteDigitalShow.keywords = keywords

                } ?: showErrorMessage("keyword")
            }

            if (isLoadedSuccessfully()) {
                loadedData.addLoadedData(LOADED_DATA_MOVIE_TV_SHOW)
                loadedData.addLoadedData(LOADED_DATA_CAST)
                loadedData.addLoadedData(LOADED_DATA_KEYWORD)
            }
        })

        //favoritedata
        detailItemViewModel.getFavoriteShow().observe(this, Observer {
            it?.let {
                when (it) {
                    is FavoriteMovie -> {
                        bindItem(it)

                        it.casts?.let { castz -> castAdapter.setData(castz.toMutableList()) }

                        keywordsHolder.removeAllViews()
                        it.keywords?.forEach { keyword -> keywordsHolder.addChip(keyword.name) }

                        swipeRefresh.isRefreshing = false
                    }
                    is FavoriteTvShow -> {
                        bindItem(it)

                        it.casts?.let { castz -> castAdapter.setData(castz.toMutableList()) }

                        keywordsHolder.removeAllViews()
                        it.keywords?.forEach { keyword -> keywordsHolder.addChip(keyword.name) }

                        swipeRefresh.isRefreshing = false
                    }
                    else -> throw Exception("Error data Type")
                }
            }
        })

        //favoritestate
        detailItemViewModel.isDataExistInDB(itemType, itemId)
            .observe(this, Observer {
                isFavorite = it
                setFavoriteIcon(isFavorite)

                //Initial data load
                //toAchieveRunOnlyOnce
                if (!detailItemViewModel.isInitialLoadDataDone) {

                    if (isFavorite) showSnackbar(R.string.data_might_be_stale, R.string.refresh) {
                        loadData(dataType, itemId, LOAD_DATA_REFRESH_DATABASE)
                    }

                    loadData(itemType, itemId, LOAD_DATA_INITIAL_LOAD)
                    detailItemViewModel.isInitialLoadDataDone = true
                    Log.d("isinitdetaill", detailItemViewModel.isInitialLoadDataDone.toString())
                }
            })

        //observing all data has been loaded or not
        loadedData.observe(AllLoadedDataSuccessfullyObserver {
            swipeRefresh.isRefreshing = false
        })
    }

    private fun loadData(dataType: Int, itemId: String, loadDataType: Int) {
        when (loadDataType) {
            LOAD_DATA_INITIAL_LOAD -> {
                if (isFavorite) {
                    loadFromDatabase(dataType, itemId)
                } else {
                    loadFromApi(dataType, itemId, disconnectedStrategy = LOAD_DATA_INITIAL_LOAD)
                }
            }
            LOAD_DATA_REFRESH -> loadFromApi(
                dataType,
                itemId,
                disconnectedStrategy = LOAD_DATA_REFRESH
            )
            LOAD_DATA_REFRESH_DATABASE -> {
                loadFromApi(dataType, itemId, disconnectedStrategy = LOAD_DATA_REFRESH_DATABASE)

                loadedData.observe(AllLoadedDataSuccessfullyObserver {
                    detailItemViewModel.addToFavorite(
                        this,
                        favoriteDigitalShow,
                        getLocale().toString(),
                        itemType,
                        itemId
                    )
                    loadedData.resetLoadDataSuccessState()
                    swipeRefresh.isRefreshing = false
                })
            }
        }
    }

    private fun loadFromApi(
        dataType: Int,
        itemId: String,
        disconnectedStrategy: Int,
        isConnected: Boolean = isConnectedToInternet(this)
    ) {
        reloadDataStrategy = disconnectedStrategy
        Log.d("connection", isConnected.toString())
        if (isConnected) {
            swipeRefresh.isRefreshing = true

            loadedData.resetLoadDataSuccessState()

            val locale = getLocale().toString()
            detailItemViewModel.loadApiData(dataType, itemId, language = locale)
        } else {
            swipeRefresh.isRefreshing = false
            showSnackbar(
                R.string.error_internet_disconnected,
                R.string.notify_when_online
            ) {
                applicationContext?.registerReceiver(
                    internetConnectionChecker,
                    intentFilterConnected
                )
            }
        }
    }

    private fun loadFromDatabase(
        dataType: Int,
        itemId: String,
        language: String = getLocale().toString()
    ) {
        swipeRefresh.isRefreshing = true

        loadedData.resetLoadDataSuccessState()

        detailItemViewModel.loadFavoriteShow(dataType, itemId, language = language)
    }

    @Suppress("DEPRECATION")
    private fun getLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) resources.configuration.locales[0] else resources.configuration.locale
    }


}

/**
 * This Class hold data loaded to monitor or observe
 * data load success state.
 * This Class will notify the Observer if all data loaded
 * successfully
 *
 * In this case: The Observer is Detail Activity itself
 */
class DataLoadedHolder : MyObservable {
    private lateinit var mObserver: AllLoadedDataSuccessfullyObserver

    override fun addObserver(observer: AllLoadedDataSuccessfullyObserver) {
        mObserver = observer
    }

    override fun notifyAllDataLoaded() {
        mObserver.onAllDataLoadedSuccessfully()
    }

    private val loadedData = mutableListOf<Int>()

    fun observe(observer: AllLoadedDataSuccessfullyObserver) {
        addObserver(observer)
    }

    fun addLoadedData(data: Int) {
        loadedData.add(data)
        if (isAllDataLoadedSuccessfully()) {
            resetLoadDataSuccessState()
            notifyAllDataLoaded()
        }
    }

    private fun isAllDataLoadedSuccessfully(): Boolean {
        return loadedData.containsAll(
            listOf(
                LOADED_DATA_MOVIE_TV_SHOW,
                LOADED_DATA_CAST,
                LOADED_DATA_KEYWORD
            )
        )
    }

    fun resetLoadDataSuccessState() {
        loadedData.clear()
    }
}