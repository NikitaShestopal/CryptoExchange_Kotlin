package com.example.cryptoexchange.worker

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.cryptoexchange.domain.model.SyncWorkStatus
import com.example.cryptoexchange.domain.scheduler.CryptoSyncScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class WorkManagerSyncScheduler(
    context: Context
) : CryptoSyncScheduler {

    private val workManager = WorkManager.getInstance(context.applicationContext)

    private val networkConstraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    override fun schedulePeriodicSync() {
        val periodicRequest = PeriodicWorkRequestBuilder<CryptoSyncWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(networkConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }

    override fun triggerManualSync() {
        val manualRequest = OneTimeWorkRequestBuilder<CryptoSyncWorker>()
            .setConstraints(networkConstraints)
            .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(
            MANUAL_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            manualRequest
        )
    }

    override fun observeSyncStatus(): Flow<SyncWorkStatus> {
        return workManager.getWorkInfosForUniqueWorkFlow(MANUAL_WORK_NAME)
            .map { infos ->
                val workInfo = infos.firstOrNull() ?: return@map SyncWorkStatus.Idle
                when (workInfo.state) {
                    WorkInfo.State.ENQUEUED,
                    WorkInfo.State.BLOCKED -> SyncWorkStatus.WaitingForNetwork
                    WorkInfo.State.RUNNING -> SyncWorkStatus.Running
                    WorkInfo.State.SUCCEEDED -> SyncWorkStatus.Succeeded
                    WorkInfo.State.FAILED -> {
                        val error = workInfo.outputData.getString(CryptoSyncWorker.KEY_ERROR_MESSAGE)
                            ?: "Не вдалося оновити дані"
                        SyncWorkStatus.Failed(error)
                    }
                    WorkInfo.State.CANCELLED -> SyncWorkStatus.Idle
                }
            }
    }

    companion object {
        const val PERIODIC_WORK_NAME = "crypto_periodic_sync"
        const val MANUAL_WORK_NAME = "crypto_manual_sync"
    }
}
