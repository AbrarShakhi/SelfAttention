package com.abrarshakhi.selfattention.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * v1 → v2 — "subject" was renamed to "course" throughout the app.
 *
 * Only the names change; every row is preserved. `RENAME COLUMN` also rewrites the composite
 * primary key that references the column, and is available from SQLite 3.25 (Android API 30 ships
 * 3.28+, and `minSdk` here is 30).
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE subjects RENAME TO courses")
        db.execSQL("ALTER TABLE attendance RENAME COLUMN subjectId TO courseId")
    }
}
