package com.example.cryptoexchange.data.mapper

import com.example.cryptoexchange.data.remote.dto.CoinGeckoAssetDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class CryptoMappersTest {

    @Test
    fun `maps dto with missing price and image safely`() {
        val dto = CoinGeckoAssetDto(
            id = "bitcoin",
            symbol = "btc",
            name = "Bitcoin",
            image = "   ",
            currentPrice = null,
            marketCapRank = 1
        )

        val entity = dto.toEntity(fallbackRank = 5, updatedAtMillis = 123456L)

        assertEquals("bitcoin", entity?.id)
        assertEquals(1, entity?.rank)
        assertEquals("Bitcoin", entity?.name)
        assertEquals("BTC", entity?.symbol)
        assertNull(entity?.logoUrl)
        assertNull(entity?.priceUsd)
    }

    @Test
    fun `maps 24h stats and uses fallback rank when rank is missing`() {
        val dto = CoinGeckoAssetDto(
            id = "ethereum",
            symbol = "eth",
            name = "Ethereum",
            image = "https://example.com/eth.png",
            currentPrice = 2500.5,
            marketCapRank = null,
            low24h = 2400.0,
            high24h = 2550.0,
            priceChangePercentage24h = -1.75,
            marketCap = 300_000_000_000.0
        )

        val entity = dto.toEntity(fallbackRank = 2, updatedAtMillis = 999L)

        assertEquals(2, entity?.rank)
        assertEquals(2400.0, entity?.low24h)
        assertEquals(2550.0, entity?.high24h)
        assertEquals(-1.75, entity?.priceChangePercent24h)
        assertEquals(300_000_000_000.0, entity?.marketCapUsd)
    }

    @Test
    fun `filters out non-finite or negative prices`() {
        val dto = CoinGeckoAssetDto(
            id = "pepe",
            symbol = "pepe",
            name = "Pepe",
            currentPrice = Double.NaN,
            low24h = -5.0,
            high24h = Double.POSITIVE_INFINITY,
            priceChangePercentage24h = Double.NaN
        )

        val entity = dto.toEntity(fallbackRank = 20, updatedAtMillis = 100L)

        assertNull(entity?.priceUsd)
        assertNull(entity?.low24h)
        assertNull(entity?.high24h)
        assertNull(entity?.priceChangePercent24h)
    }

    @Test
    fun `returns null when id is missing`() {
        val dto = CoinGeckoAssetDto(id = null, symbol = "eth", name = "Ethereum")
        assertNull(dto.toEntity(fallbackRank = 2, updatedAtMillis = 123456L))
    }
}
