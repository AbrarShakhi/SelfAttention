package com.abrarshakhi.selfattention.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE subjects RENAME TO courses")
        db.execSQL("ALTER TABLE attendance RENAME COLUMN subjectId TO courseId")
    }
}
