package com.mindshift.anxiety.data.repository

import com.mindshift.anxiety.data.local.ClickDao
import com.mindshift.anxiety.data.local.ClickEntity
import com.mindshift.anxiety.data.preferences.UserPreferences
import com.mindshift.anxiety.data.remote.ApiService
import com.mindshift.anxiety.data.remote.models.ClickRequest
import com.mindshift.anxiety.data.remote.models.SyncRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClickRepository @Inject constructor(
    private val clickDao: ClickDao,
    private val apiService: ApiService,
    private val userPreferences: UserPreferences
) {
    private val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    fun getUnsyncedCount(): Flow<Int> = clickDao.getUnsyncedCount()

    suspend fun recordClick() {
        val userId = userPreferences.userId.first() ?: return
        val click = ClickEntity(
            userId = userId,
            clickedAt = isoFormat.format(Date()),
            synced = false
        )
        clickDao.insert(click)
    }

    suspend fun syncPendingClicks(): Result<Int> {
        return try {
            val token = userPreferences.accessToken.first()
                ?: return Result.failure(Exception("No autenticado"))

            val unsynced = clickDao.getUnsyncedClicksOnce()
            if (unsynced.isEmpty()) return Result.success(0)

            val request = SyncRequest(
                clicks = unsynced.map { ClickRequest(it.clickedAt) }
            )
            val response = apiService.syncClicks("Bearer $token", request)

            clickDao.markAsSynced(unsynced.map { it.id })
            userPreferences.saveLastSync(isoFormat.format(Date()))

            Result.success(response.queued)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
