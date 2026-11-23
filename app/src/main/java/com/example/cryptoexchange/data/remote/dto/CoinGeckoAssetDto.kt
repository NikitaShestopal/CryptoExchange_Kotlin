package com.example.cryptoexchange.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CoinGeckoAssetDto(
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("symbol")
    val symbol: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("image")
    val image: String? = null,
    @SerializedName("current_price")
    val currentPrice: Double? = null,
    @SerializedName("market_cap_rank")
    val marketCapRank: Int? = null
)
