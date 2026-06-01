package com.mindshift.anxiety.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.mindshift.anxiety.data.repository.ClickRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val clickRepository: ClickRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            clickRepository.syncPendingClicks()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "mindshift_sync_worker"
    }
}
