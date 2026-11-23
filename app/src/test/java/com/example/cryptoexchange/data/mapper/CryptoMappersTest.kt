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
    fun `returns null when id is missing`() {
        val dto = CoinGeckoAssetDto(id = null, symbol = "eth", name = "Ethereum")
        assertNull(dto.toEntity(fallbackRank = 2, updatedAtMillis = 123456L))
    }
}
