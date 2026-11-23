package com.example.cryptoexchange.data.mapper

import com.example.cryptoexchange.data.local.CryptoAssetEntity
import com.example.cryptoexchange.data.remote.dto.CoinGeckoAssetDto
import com.example.cryptoexchange.domain.model.CryptoAsset
import java.util.Locale

fun CoinGeckoAssetDto.toEntity(
    fallbackRank: Int,
    updatedAtMillis: Long
): CryptoAssetEntity? {
    val stableId = id?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val cleanSymbol = symbol?.trim()?.uppercase(Locale.US).orEmpty()
    val cleanName = name?.trim().orEmpty()
    if (cleanSymbol.isEmpty() && cleanName.isEmpty()) return null

    val validPrice = currentPrice?.takeIf { it.isFinite() && it >= 0.0 }
    val validLow = low24h?.takeIf { it.isFinite() && it >= 0.0 }
    val validHigh = high24h?.takeIf { it.isFinite() && it >= 0.0 }
    val validChange = priceChangePercentage24h?.takeIf { it.isFinite() }
    val validMarketCap = marketCap?.takeIf { it.isFinite() && it >= 0.0 }
    val cleanLogo = image?.trim()?.takeIf { it.isNotEmpty() }
    val resolvedRank = marketCapRank?.takeIf { it > 0 } ?: fallbackRank

    return CryptoAssetEntity(
        id = stableId,
        rank = resolvedRank,
        name = cleanName.ifEmpty { cleanSymbol },
        symbol = cleanSymbol.ifEmpty { cleanName },
        logoUrl = cleanLogo,
        priceUsd = validPrice,
        updatedAtMillis = updatedAtMillis,
        low24h = validLow,
        high24h = validHigh,
        priceChangePercent24h = validChange,
        marketCapUsd = validMarketCap
    )
}

fun CryptoAssetEntity.toDomain(): CryptoAsset = CryptoAsset(
    id = id,
    rank = rank,
    name = name,
    symbol = symbol,
    logoUrl = logoUrl,
    priceUsd = priceUsd,
    updatedAtMillis = updatedAtMillis,
    low24h = low24h,
    high24h = high24h,
    priceChangePercent24h = priceChangePercent24h,
    marketCapUsd = marketCapUsd
)
