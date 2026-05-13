package com.bucket.data.datasource.di

import com.bucket.data.datasource.category.CategoryDataSource
import com.bucket.data.datasource.category.CategoryRemoteDataSource
import com.bucket.data.datasource.home.HomeDataSource
import com.bucket.data.datasource.home.HomeRemoteDataSource
import com.bucket.data.datasource.post.PostDataSource
import com.bucket.data.datasource.post.PostRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {
    @Binds
    @Singleton
    abstract fun bindHomeDataSource(
        homeRemoteDataSource: HomeRemoteDataSource
    ): HomeDataSource

    @Binds
    @Singleton
    abstract fun bindCategoryDataSource(
        categoryRemoteDataSource: CategoryRemoteDataSource
    ): CategoryDataSource

    @Binds
    @Singleton
    abstract fun bindPostDataSource(
        postRemoteDataSource: PostRemoteDataSource
    ): PostDataSource
}
