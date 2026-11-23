package com.example.cryptoexchange.presentation.state

import com.example.cryptoexchange.domain.model.CryptoAsset
import com.example.cryptoexchange.domain.model.SyncWorkStatus

data class CryptoUiState(
    val assets: List<CryptoAsset> = emptyList(),
    val syncStatus: SyncWorkStatus = SyncWorkStatus.Idle,
    val lastUpdatedMillis: Long? = null
) {
    val isEmpty: Boolean get() = assets.isEmpty()
    val isRefreshing: Boolean get() = syncStatus is SyncWorkStatus.Running
}
