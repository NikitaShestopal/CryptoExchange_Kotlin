package com.example.cryptoexchange.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.cryptoexchange.CryptoExchangeApp
import com.example.cryptoexchange.domain.model.SyncResult

class CryptoSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as CryptoExchangeApp
        val syncUseCase = app.container.syncCryptoAssetsUseCase

        return when (val result = syncUseCase()) {
            is SyncResult.Success -> Result.success()

            is SyncResult.AuthError -> {
                Result.failure(workDataOf(KEY_ERROR_MESSAGE to result.message))
            }

            is SyncResult.RateLimitError -> {
                if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                    Result.retry()
                } else {
                    Result.failure(workDataOf(KEY_ERROR_MESSAGE to result.message))
                }
            }

            is SyncResult.NetworkError -> {
                if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                    Result.retry()
                } else {
                    Result.failure(workDataOf(KEY_ERROR_MESSAGE to result.message))
                }
            }

            is SyncResult.UnknownError -> {
                Result.failure(workDataOf(KEY_ERROR_MESSAGE to result.message))
            }
        }
    }

    companion object {
        const val KEY_ERROR_MESSAGE = "error_message"
        private const val MAX_RETRY_ATTEMPTS = 3
    }
}
