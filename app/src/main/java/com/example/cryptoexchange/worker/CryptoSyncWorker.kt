package com.example.cryptoexchange.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.cryptoexchange.BuildConfig
import com.example.cryptoexchange.data.local.CryptoDatabase
import com.example.cryptoexchange.data.remote.CoinGeckoNetworkFactory
import com.example.cryptoexchange.data.repository.CryptoRepositoryImpl
import com.example.cryptoexchange.domain.model.SyncResult

class CryptoSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val api = CoinGeckoNetworkFactory.createApi(
            apiKey = BuildConfig.COINGECKO_API_KEY,
            isDebug = BuildConfig.DEBUG
        )
        val dao = CryptoDatabase.build(applicationContext).cryptoDao()
        val repository = CryptoRepositoryImpl(api = api, dao = dao)

        return when (val result = repository.syncTopAssets()) {
            is SyncResult.Success -> Result.success()

            is SyncResult.AuthError -> {
                Result.failure(workDataOf(KEY_ERROR_MESSAGE to result.message))
            }

            is SyncResult.RateLimitError,
            is SyncResult.NetworkError -> {
                val msg = when (result) {
                    is SyncResult.RateLimitError -> result.message
                    is SyncResult.NetworkError -> result.message
                    else -> "Помилка мережі"
                }
                if (runAttemptCount < MAX_RETRY_ATTEMPTS) {
                    Result.retry()
                } else {
                    Result.failure(workDataOf(KEY_ERROR_MESSAGE to msg))
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