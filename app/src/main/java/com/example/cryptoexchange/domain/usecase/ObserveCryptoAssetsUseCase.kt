package com.example.cryptoexchange.domain.usecase

import com.example.cryptoexchange.domain.model.CryptoAsset
import com.example.cryptoexchange.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow

class ObserveCryptoAssetsUseCase(
    private val repository: CryptoRepository
) {
    operator fun invoke(): Flow<List<CryptoAsset>> = repository.observeTopAssets()
}
