package com.example.candra.favoritemovie2

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import com.example.candra.data.model.DigitalShowDetails
import com.example.candra.favoritefeature2.viewmodel.FavoriteDigitalShowViewModel

class FavoriteDigitalShowViewModelOutsideSource(application: Application) :
    FavoriteDigitalShowViewModel(application) {
    private val favoriteContentProviderRepo =
        FavoriteRepoFromContextProvider()

    override fun getSource(
        context: Context,
        dataType: Int
    ): LiveData<List<DigitalShowDetails>> {
        return favoriteContentProviderRepo.loadFavoriteDigitalShowFromContentProvider(
            context,
            dataType
        )
    }
}