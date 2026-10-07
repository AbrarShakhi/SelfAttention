package com.abrarshakhi.selfattention.core.common.di

import com.abrarshakhi.selfattention.core.common.coroutine.ApplicationScope
import com.abrarshakhi.selfattention.core.common.time.SystemTimeTicker
import com.abrarshakhi.selfattention.core.common.time.TimeTicker
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {

    @Binds
    abstract fun bindTimeTicker(ticker: SystemTimeTicker): TimeTicker

    companion object {
        @Provides
        @Singleton
        @ApplicationScope
        fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}
