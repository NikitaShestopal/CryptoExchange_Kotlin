package com.example.cryptoexchange.domain.scheduler

import com.example.cryptoexchange.domain.model.SyncWorkStatus
import kotlinx.coroutines.flow.Flow

interface CryptoSyncScheduler {
    fun schedulePeriodicSync()
    fun triggerManualSync()
    fun observeSyncStatus(): Flow<SyncWorkStatus>
}
