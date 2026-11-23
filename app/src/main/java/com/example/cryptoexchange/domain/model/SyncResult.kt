package com.example.cryptoexchange.domain.model

sealed interface SyncResult {
    data object Success : SyncResult
    data class AuthError(val message: String) : SyncResult
    data class RateLimitError(val message: String, val retryAfterSeconds: Long? = null) : SyncResult
    data class NetworkError(val message: String) : SyncResult
    data class UnknownError(val message: String) : SyncResult
}
