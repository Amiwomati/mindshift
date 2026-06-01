package com.mindshift.anxiety.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClickDao {
    @Insert
    suspend fun insert(click: ClickEntity)

    @Query("SELECT * FROM clicks WHERE synced = 0")
    fun getUnsyncedClicks(): Flow<List<ClickEntity>>

    @Query("SELECT * FROM clicks WHERE synced = 0")
    suspend fun getUnsyncedClicksOnce(): List<ClickEntity>

    @Query("SELECT COUNT(*) FROM clicks WHERE synced = 0")
    fun getUnsyncedCount(): Flow<Int>

    @Query("UPDATE clicks SET synced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<Long>)
}
