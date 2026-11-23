package com.example.cryptoexchange.presentation.util

import org.junit.Assert.assertEquals
import org.junit.Test

class PriceFormatterTest {

    @Test
    fun `returns no data label when price is null`() {
        assertEquals("Немає даних", PriceFormatter.formatUsd(null))
    }

    @Test
    fun `does not round small prices to zero`() {
        assertEquals("$0.00002505", PriceFormatter.formatUsd(0.00002505))
    }

    @Test
    fun `formats standard price cleanly`() {
        assertEquals("$95432.15", PriceFormatter.formatUsd(95432.15))
    }

    @Test
    fun `formats 24h percent change with sign`() {
        assertEquals("+3.46%", PriceFormatter.formatPercentChange(3.456))
        assertEquals("-1.20%", PriceFormatter.formatPercentChange(-1.2))
        assertEquals("—", PriceFormatter.formatPercentChange(null))
    }

    @Test
    fun `formats compact market cap`() {
        assertEquals("$1.25T", PriceFormatter.formatMarketCap(1_250_000_000_000.0))
        assertEquals("$84.50B", PriceFormatter.formatMarketCap(84_500_000_000.0))
        assertEquals("Немає даних", PriceFormatter.formatMarketCap(null))
    }
}