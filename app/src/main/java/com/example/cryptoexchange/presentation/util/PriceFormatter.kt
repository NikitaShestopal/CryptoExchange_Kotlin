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

    fun formatPercentChange(change: Double?): String {
        if (change == null || !change.isFinite()) return "—"
        val rounded = BigDecimal.valueOf(change)
            .setScale(2, RoundingMode.HALF_UP)
            .toPlainString()
        return if (change > 0) "+$rounded%" else "$rounded%"
    }

    fun formatMarketCap(capUsd: Double?): String {
        if (capUsd == null || !capUsd.isFinite() || capUsd <= 0.0) return "Немає даних"
        return when {
            capUsd >= 1_000_000_000_000.0 -> String.format(Locale.US, "$%.2fT", capUsd / 1_000_000_000_000.0)
            capUsd >= 1_000_000_000.0 -> String.format(Locale.US, "$%.2fB", capUsd / 1_000_000_000.0)
            capUsd >= 1_000_000.0 -> String.format(Locale.US, "$%.2fM", capUsd / 1_000_000.0)
            else -> formatUsd(capUsd)
        }
    }

    fun formatTimestamp(millis: Long?): String {
        if (millis == null || millis <= 0L) return "Ще не оновлювалося"
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(Date(millis))
    }
}
