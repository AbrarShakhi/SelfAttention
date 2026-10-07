package com.abrarshakhi.selfattention.core.data.di

import com.abrarshakhi.selfattention.core.data.backup.BackupCodec
import com.abrarshakhi.selfattention.core.data.backup.BackupFileStore
import com.abrarshakhi.selfattention.core.data.backup.ContentResolverBackupFileStore
import com.abrarshakhi.selfattention.core.data.backup.JsonBackupCodec
import com.abrarshakhi.selfattention.core.data.repository.AttendanceRepository
import com.abrarshakhi.selfattention.core.data.repository.CourseRepository
import com.abrarshakhi.selfattention.core.data.repository.DefaultAttendanceRepository
import com.abrarshakhi.selfattention.core.data.repository.DefaultCourseRepository
import com.abrarshakhi.selfattention.core.data.repository.DefaultSettingsRepository
import com.abrarshakhi.selfattention.core.data.repository.SettingsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    @Singleton
    abstract fun bindCourseRepository(repository: DefaultCourseRepository): CourseRepository

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(repository: DefaultAttendanceRepository): AttendanceRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(repository: DefaultSettingsRepository): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindBackupCodec(codec: JsonBackupCodec): BackupCodec

    @Binds
    @Singleton
    abstract fun bindBackupFileStore(store: ContentResolverBackupFileStore): BackupFileStore
}
