package com.example.cryptoexchange.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cryptoexchange.domain.scheduler.CryptoSyncScheduler
import com.example.cryptoexchange.domain.usecase.ObserveCryptoAssetsUseCase
import com.example.cryptoexchange.domain.usecase.TriggerManualSyncUseCase
import com.example.cryptoexchange.presentation.state.CryptoUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class CryptoViewModel(
    observeCryptoAssetsUseCase: ObserveCryptoAssetsUseCase,
    private val triggerManualSyncUseCase: TriggerManualSyncUseCase,
    syncScheduler: CryptoSyncScheduler
) : ViewModel() {

    val uiState: StateFlow<CryptoUiState> = combine(
        observeCryptoAssetsUseCase(),
        syncScheduler.observeSyncStatus()
    ) { assets, status ->
        CryptoUiState(
            assets = assets,
            syncStatus = status,
            lastUpdatedMillis = assets.maxOfOrNull { it.updatedAtMillis }
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CryptoUiState()
    )

    fun onRefreshClick() {
        triggerManualSyncUseCase()
    }

    companion object {
        fun factory(
            observeCryptoAssetsUseCase: ObserveCryptoAssetsUseCase,
            triggerManualSyncUseCase: TriggerManualSyncUseCase,
            syncScheduler: CryptoSyncScheduler
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return CryptoViewModel(
                    observeCryptoAssetsUseCase = observeCryptoAssetsUseCase,
                    triggerManualSyncUseCase = triggerManualSyncUseCase,
                    syncScheduler = syncScheduler
                ) as T
            }
        }
    }
}
