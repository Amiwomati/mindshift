package com.mindshift.anxiety.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ClickEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ClickDatabase : RoomDatabase() {
    abstract fun clickDao(): ClickDao
}
