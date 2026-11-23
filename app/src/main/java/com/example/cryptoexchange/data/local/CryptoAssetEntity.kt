package com.example.cryptoexchange.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "crypto_assets")
data class CryptoAssetEntity(
    @PrimaryKey
    val id: String,
    val rank: Int,
    val name: String,
    val symbol: String,
    val logoUrl: String?,
    val priceUsd: Double?,
    val updatedAtMillis: Long,
    val low24h: Double? = null,
    val high24h: Double? = null,
    val priceChangePercent24h: Double? = null,
    val marketCapUsd: Double? = null
)
