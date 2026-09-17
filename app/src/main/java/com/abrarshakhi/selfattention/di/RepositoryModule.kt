package com.abrarshakhi.selfattention.di

import android.app.AlarmManager
import android.content.Context
import com.abrarshakhi.selfattention.data.alarm.AlarmSchedulerImpl
import com.abrarshakhi.selfattention.data.backup.JsonBackupCodec
import com.abrarshakhi.selfattention.data.repository.AttendanceRepositoryImpl
import com.abrarshakhi.selfattention.data.repository.SettingsRepositoryImpl
import com.abrarshakhi.selfattention.data.repository.CourseRepositoryImpl
import com.abrarshakhi.selfattention.domain.alarm.AlarmScheduler
import com.abrarshakhi.selfattention.domain.backup.BackupCodec
import com.abrarshakhi.selfattention.domain.repository.AttendanceRepository
import com.abrarshakhi.selfattention.domain.repository.SettingsRepository
import com.abrarshakhi.selfattention.domain.repository.CourseRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindCourseRepository(impl: CourseRepositoryImpl): CourseRepository

    @Binds
    @Singleton
    abstract fun bindAttendanceRepository(impl: AttendanceRepositoryImpl): AttendanceRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(impl: AlarmSchedulerImpl): AlarmScheduler

    @Binds
    @Singleton
    abstract fun bindBackupCodec(impl: JsonBackupCodec): BackupCodec
}

@Module
@InstallIn(SingletonComponent::class)
object AlarmManagerModule {

    @Provides
    @Singleton
    fun provideAlarmManager(@ApplicationContext context: Context): AlarmManager =
        context.getSystemService(AlarmManager::class.java)
}
