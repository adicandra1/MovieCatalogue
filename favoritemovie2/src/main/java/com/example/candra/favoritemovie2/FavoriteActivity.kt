package com.example.candra.favoritemovie2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class FavoriteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)

        loadFragment(savedInstanceState, FavoriteTabHolderOutsideSource(), FRAGMENT_FAVORITE)

    }

    @Suppress("SameParameterValue")
    private fun loadFragment(savedInstanceState: Bundle?, fragment: Fragment, key: String) {
        if (savedInstanceState == null && supportFragmentManager.findFragmentByTag(key) == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.main_content, fragment, key)
                .commit()
        }
    }

    companion object {
        private const val FRAGMENT_FAVORITE = "favoriteFragment"
    }
}
