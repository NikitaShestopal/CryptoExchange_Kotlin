package com.example.cryptoexchange.presentation.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cryptoexchange.presentation.state.CryptoUiState
import com.example.cryptoexchange.presentation.ui.components.CryptoAssetCard
import com.example.cryptoexchange.presentation.ui.components.CryptoDetailPane
import com.example.cryptoexchange.presentation.ui.components.SyncStatusBanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CryptoListScreen(
    uiState: CryptoUiState,
    onRefreshClick: () -> Unit,
    onAssetClick: (String) -> Unit,
    onBackFromDetail: () -> Unit,
    onSearchQueryChange: (String) -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val showPortraitDetail = !isLandscape && uiState.selectedAsset != null

    if (showPortraitDetail) {
        BackHandler(onBack = onBackFromDetail)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    if (showPortraitDetail) {
                        IconButton(onClick = onBackFromDetail) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад"
                            )
                        }
                    }
                },
                title = {
                    Text(
                        text = if (showPortraitDetail) {
                            "${uiState.selectedAsset?.symbol} / USD"
                        } else {
                            "Ринки • Top 30"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onRefreshClick) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Оновити",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .displayCutoutPadding()
        ) {
            SyncStatusBanner(
                lastUpdatedMillis = uiState.lastUpdatedMillis,
                syncStatus = uiState.syncStatus
            )

            when {
                uiState.isEmpty -> {
                    EmptyState(
                        onRefreshClick = onRefreshClick,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                showPortraitDetail -> {
                    uiState.selectedAsset?.let { asset ->
                        CryptoDetailPane(asset = asset)
                    }
                }

                isLandscape -> {
                    Row(modifier = Modifier.fillMaxSize()) {
                        AssetListColumn(
                            uiState = uiState,
                            onAssetClick = onAssetClick,
                            onSearchQueryChange = onSearchQueryChange,
                            modifier = Modifier
                                .weight(0.46f)
                                .fillMaxHeight()
                        )
                        VerticalDivider(
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        val detailAsset = uiState.selectedAsset ?: uiState.filteredAssets.firstOrNull()
                        Box(
                            modifier = Modifier
                                .weight(0.54f)
                                .fillMaxHeight()
                        ) {
                            if (detailAsset != null) {
                                CryptoDetailPane(asset = detailAsset)
                            }
                        }
                    }
                }

                else -> {
                    AssetListColumn(
                        uiState = uiState,
                        onAssetClick = onAssetClick,
                        onSearchQueryChange = onSearchQueryChange,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun AssetListColumn(
    uiState: CryptoUiState,
    onAssetClick: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.background(MaterialTheme.colorScheme.background)) {
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = { Text("Пошук (BTC, Ethereum...)") },
            singleLine = true,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
            },
            trailingIcon = {
                if (uiState.searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChange("") }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Очистити")
                    }
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(
                items = uiState.filteredAssets,
                key = { it.id }
            ) { asset ->
                CryptoAssetCard(
                    asset = asset,
                    isSelected = asset.id == uiState.selectedAssetId,
                    onClick = { onAssetClick(asset.id) }
                )
            }
        }
    }
}

@Composable
private fun EmptyState(
    onRefreshClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Дані ще не завантажені",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Натисніть «Оновити», щоб завантажити топ-30 криптовалют із CoinGecko API.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(onClick = onRefreshClick) {
                Text("Оновити")
            }
        }
    }
}
