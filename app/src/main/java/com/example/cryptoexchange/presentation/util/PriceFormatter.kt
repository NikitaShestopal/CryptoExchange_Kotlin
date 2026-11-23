package com.example.cryptoexchange.presentation.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PriceFormatter {

    fun formatUsd(price: Double?): String {
        if (price == null || !price.isFinite()) return "Немає даних"

        val bd = BigDecimal.valueOf(price)
        val formatted = when {
            price >= 1.0 -> bd.setScale(2, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
            price >= 0.01 -> bd.setScale(4, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
            price > 0.0 -> bd.setScale(8, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()
            else -> "0"
        }
        return "$$formatted"
    }

    fun formatTimestamp(millis: Long?): String {
        if (millis == null || millis <= 0L) return "Ще не оновлювалося"
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}
