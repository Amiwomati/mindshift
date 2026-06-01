package com.mindshift.anxiety.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clicks")
data class ClickEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: Int,
    @ColumnInfo(name = "clicked_at")
    val clickedAt: String,
    @ColumnInfo(name = "synced")
    val synced: Boolean = false
)
