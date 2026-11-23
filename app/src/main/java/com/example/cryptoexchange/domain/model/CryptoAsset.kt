package com.example.cryptoexchange.domain.model

data class CryptoAsset(
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val logoUrl: String?,
    val priceUsd: Double?,
    val updatedAtMillis: Long
)
