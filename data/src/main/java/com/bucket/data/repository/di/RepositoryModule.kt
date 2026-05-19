package com.bucket.data.repository.di

import com.bucket.data.repository.AuthRepositoryImpl
import com.bucket.data.repository.CategoryRepositoryImpl
import com.bucket.data.repository.HomeRepositoryImpl
import com.bucket.data.repository.PostRepositoryImpl
import com.bucket.data.repository.ProfileRepositoryImpl
import com.example.domain.repository.auth.AuthRepository
import com.example.domain.repository.category.CategoryRepository
import com.example.domain.repository.home.HomeRepository
import com.example.domain.repository.post.PostRepository
import com.example.domain.repository.profile.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(
        homeRepositoryImpl: HomeRepositoryImpl
    ): HomeRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindPostRepository(
        postRepositoryImpl: PostRepositoryImpl
    ): PostRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository
}
