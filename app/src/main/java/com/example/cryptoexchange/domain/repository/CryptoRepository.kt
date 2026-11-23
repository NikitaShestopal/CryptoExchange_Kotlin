package com.example.cryptoexchange.domain.repository

import com.example.cryptoexchange.domain.model.CryptoAsset
import com.example.cryptoexchange.domain.model.SyncResult
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {
    fun observeTopAssets(): Flow<List<CryptoAsset>>
    suspend fun syncTopAssets(): SyncResult
}
