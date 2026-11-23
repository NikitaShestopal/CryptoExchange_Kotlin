package com.example.cryptoexchange.presentation.state

import com.example.cryptoexchange.domain.model.CryptoAsset
import com.example.cryptoexchange.domain.model.SyncWorkStatus

data class CryptoUiState(
    val assets: List<CryptoAsset> = emptyList(),
    val syncStatus: SyncWorkStatus = SyncWorkStatus.Idle,
    val lastUpdatedMillis: Long? = null,
    val selectedAssetId: String? = null,
    val searchQuery: String = ""
) {
    val isEmpty: Boolean get() = assets.isEmpty()
    val isRefreshing: Boolean get() = syncStatus is SyncWorkStatus.Running

    val filteredAssets: List<CryptoAsset>
        get() {
            val q = searchQuery.trim()
            if (q.isEmpty()) return assets
            return assets.filter {
                it.name.contains(q, ignoreCase = true) ||
                    it.symbol.contains(q, ignoreCase = true)
            }
        }

    val selectedAsset: CryptoAsset?
        get() = assets.firstOrNull { it.id == selectedAssetId }
}
