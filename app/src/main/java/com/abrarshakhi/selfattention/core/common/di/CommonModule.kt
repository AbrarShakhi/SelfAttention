package com.abrarshakhi.selfattention.core.common.di

import com.abrarshakhi.selfattention.core.common.time.SystemTimeTicker
import com.abrarshakhi.selfattention.core.common.time.TimeTicker
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {
    @Binds
    abstract fun bindTimeTicker(ticker: SystemTimeTicker): TimeTicker
}
