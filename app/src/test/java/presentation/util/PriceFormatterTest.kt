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
}
