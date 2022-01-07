package com.example.candra.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.candra.data.model.FavoriteMovie
import com.example.candra.data.model.FavoriteTvShow
import com.example.candra.data.model.ReleaseToday

@Database(
    entities = [FavoriteMovie::class, FavoriteTvShow::class, ReleaseToday::class],
    version = 4
)
@TypeConverters(Converters::class)
abstract class FavoriteRoomDatabase : RoomDatabase() {

    abstract fun favoriteMovieDao(): FavoriteMovieDao
    abstract fun favoriteTvShowDao(): FavoriteTvShowDao
    abstract fun releaseTodayDao(): ReleaseTodayDao

    companion object {
        // Singleton prevents multiple instance of database opening at the
        // same time
        @Volatile
        private var INSTANCE: FavoriteRoomDatabase? = null

        fun getDatabase(context: Context): FavoriteRoomDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavoriteRoomDatabase::class.java,
                    "favorite_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

}