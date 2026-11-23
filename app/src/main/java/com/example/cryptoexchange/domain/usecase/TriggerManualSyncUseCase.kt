package com.example.cryptoexchange.domain.usecase

import com.example.cryptoexchange.domain.scheduler.CryptoSyncScheduler

class TriggerManualSyncUseCase(
    private val scheduler: CryptoSyncScheduler
) {
    operator fun invoke() = scheduler.triggerManualSync()
}
