package com.shevaalex.android.plugev.data.postcodesio.di

import com.shevaalex.android.plugev.data.postcodesio.PostCodeRepositoryImpl
import com.shevaalex.android.plugev.domain.postcode.repository.PostCodeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class PostCodeDataBinderModule {

    @Binds
    @ViewModelScoped
    abstract fun bindPostCodeRepo(repo: PostCodeRepositoryImpl): PostCodeRepository

}
