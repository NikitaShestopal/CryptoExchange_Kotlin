package com.example.cryptoexchange.domain.model

sealed interface SyncWorkStatus {
    data object Idle : SyncWorkStatus
    data object WaitingForNetwork : SyncWorkStatus
    data object Running : SyncWorkStatus
    data object Succeeded : SyncWorkStatus
    data class Failed(val message: String) : SyncWorkStatus
}
