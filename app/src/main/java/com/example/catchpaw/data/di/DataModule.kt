package com.example.catchpaw.data.di

import android.content.Context
import androidx.room.Room
import com.example.catchpaw.data.local.CatchPawDatabase
import com.example.catchpaw.data.local.GameScoreDao
import com.example.catchpaw.data.repository.ScoreRepositoryImpl
import com.example.catchpaw.domain.repository.ScoreRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindScoreRepository(impl: ScoreRepositoryImpl): ScoreRepository

    companion object {

        @Provides
        @Singleton
        fun provideDatabase(@ApplicationContext context: Context): CatchPawDatabase =
            Room.databaseBuilder(
                context,
                CatchPawDatabase::class.java,
                "catchpaw.db"
            ).build()

        @Provides
        @Singleton
        fun provideGameScoreDao(database: CatchPawDatabase): GameScoreDao =
            database.gameScoreDao()
    }
}
