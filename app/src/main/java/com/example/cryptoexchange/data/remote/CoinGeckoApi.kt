package com.example.cryptoexchange.data.remote

import com.example.cryptoexchange.data.remote.dto.CoinGeckoAssetDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoApi {

    @GET("coins/markets")
    suspend fun getTopAssets(
        @Query("vs_currency") vsCurrency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 30,
        @Query("page") page: Int = 1
    ): Response<List<CoinGeckoAssetDto>>

    companion object {
        const val BASE_URL = "https://api.coingecko.com/api/v3/"
    }
}
