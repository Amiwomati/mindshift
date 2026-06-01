package com.mindshift.anxiety.di

import com.mindshift.anxiety.data.local.ClickDao
import com.mindshift.anxiety.data.preferences.UserPreferences
import com.mindshift.anxiety.data.remote.ApiService
import com.mindshift.anxiety.data.repository.AuthRepository
import com.mindshift.anxiety.data.repository.ClickRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        apiService: ApiService,
        userPreferences: UserPreferences
    ): AuthRepository = AuthRepository(apiService, userPreferences)

    @Provides
    @Singleton
    fun provideClickRepository(
        clickDao: ClickDao,
        apiService: ApiService,
        userPreferences: UserPreferences
    ): ClickRepository = ClickRepository(clickDao, apiService, userPreferences)
}
