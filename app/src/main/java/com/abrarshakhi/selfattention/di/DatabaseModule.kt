package com.abrarshakhi.selfattention.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.abrarshakhi.selfattention.data.local.AppDatabase
import com.abrarshakhi.selfattention.data.local.MIGRATION_1_2
import com.abrarshakhi.selfattention.data.local.dao.AttendanceDao
import com.abrarshakhi.selfattention.data.local.dao.CourseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "selfattention.db")
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideCourseDao(db: AppDatabase): CourseDao = db.courseDao()

    @Provides
    fun provideAttendanceDao(db: AppDatabase): AttendanceDao = db.attendanceDao()

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore
}
