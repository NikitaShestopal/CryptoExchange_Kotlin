package com.example.cryptoexchange.di

import android.content.Context
import com.example.cryptoexchange.BuildConfig
import com.example.cryptoexchange.data.local.CryptoDatabase
import com.example.cryptoexchange.data.remote.CoinGeckoNetworkFactory
import com.example.cryptoexchange.data.repository.CryptoRepositoryImpl
import com.example.cryptoexchange.domain.repository.CryptoRepository
import com.example.cryptoexchange.domain.scheduler.CryptoSyncScheduler
import com.example.cryptoexchange.domain.usecase.ObserveCryptoAssetsUseCase
import com.example.cryptoexchange.domain.usecase.SyncCryptoAssetsUseCase
import com.example.cryptoexchange.domain.usecase.TriggerManualSyncUseCase
import com.example.cryptoexchange.worker.WorkManagerSyncScheduler

class AppContainer(context: Context) {

    private val database: CryptoDatabase by lazy {
        CryptoDatabase.build(context)
    }

    private val api by lazy {
        CoinGeckoNetworkFactory.createApi(
            apiKey = BuildConfig.COINGECKO_API_KEY,
            isDebug = BuildConfig.DEBUG
        )
    }

    val cryptoRepository: CryptoRepository by lazy {
        CryptoRepositoryImpl(
            api = api,
            dao = database.cryptoDao()
        )
    }

    val syncScheduler: CryptoSyncScheduler by lazy {
        WorkManagerSyncScheduler(context)
    }

    val observeCryptoAssetsUseCase: ObserveCryptoAssetsUseCase by lazy {
        ObserveCryptoAssetsUseCase(cryptoRepository)
    }

    val syncCryptoAssetsUseCase: SyncCryptoAssetsUseCase by lazy {
        SyncCryptoAssetsUseCase(cryptoRepository)
    }

    val triggerManualSyncUseCase: TriggerManualSyncUseCase by lazy {
        TriggerManualSyncUseCase(syncScheduler)
    }
}
