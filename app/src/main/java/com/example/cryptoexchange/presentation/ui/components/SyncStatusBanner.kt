package com.example.cryptoexchange.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.cryptoexchange.domain.model.SyncWorkStatus
import com.example.cryptoexchange.presentation.ui.theme.ExchangeColors
import com.example.cryptoexchange.presentation.util.PriceFormatter

@Composable
fun SyncStatusBanner(
    lastUpdatedMillis: Long?,
    syncStatus: SyncWorkStatus,
    modifier: Modifier = Modifier
) {
    val dotColor = when (syncStatus) {
        is SyncWorkStatus.Running -> MaterialTheme.colorScheme.primary
        is SyncWorkStatus.WaitingForNetwork -> MaterialTheme.colorScheme.secondary
        is SyncWorkStatus.Failed -> ExchangeColors.Negative
        is SyncWorkStatus.Succeeded,
        is SyncWorkStatus.Idle -> ExchangeColors.Positive
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (syncStatus is SyncWorkStatus.Running) {
                CircularProgressIndicator(
                    modifier = Modifier.size(12.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Оновлено: ${PriceFormatter.formatTimestamp(lastUpdatedMillis)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                when (syncStatus) {
                    is SyncWorkStatus.WaitingForNetwork -> {
                        Text(
                            text = "Очікування мережі...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    is SyncWorkStatus.Running -> {
                        Text(
                            text = "Синхронізація курсів...",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    is SyncWorkStatus.Failed -> {
                        Text(
                            text = syncStatus.message,
                            style = MaterialTheme.typography.labelSmall,
                            color = ExchangeColors.Negative
                        )
                    }

                    is SyncWorkStatus.Succeeded,
                    is SyncWorkStatus.Idle -> Unit
                }
            }
        }

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
