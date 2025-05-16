package com.vishwaagrotech.digitalhr.di

import android.content.Context
import com.vishwaagrotech.digitalhr.repository.network.RetrofitDataSource
import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 *Copyright (c) 2022, All Rights Reserved.
 */


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideApiService(retrofitDataSource: RetrofitDataSource): RetrofitService {
        return retrofitDataSource.getInstance()
    }

    @Provides
    @Singleton
    fun provideFusionClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }
}