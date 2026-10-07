package com.abrarshakhi.selfattention.core.database.di

import android.content.Context
import androidx.room.Room
import com.abrarshakhi.selfattention.core.database.AppDatabase
import com.abrarshakhi.selfattention.core.database.MIGRATION_1_2
import com.abrarshakhi.selfattention.core.database.dao.AttendanceDao
import com.abrarshakhi.selfattention.core.database.dao.CourseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
    fun provideCourseDao(database: AppDatabase): CourseDao = database.courseDao()

    @Provides
    fun provideAttendanceDao(database: AppDatabase): AttendanceDao = database.attendanceDao()
}
