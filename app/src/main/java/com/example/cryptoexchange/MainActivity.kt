package com.example.cryptoexchange

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cryptoexchange.presentation.ui.CryptoListScreen
import com.example.cryptoexchange.presentation.ui.theme.CryptoExchangeTheme
import com.example.cryptoexchange.presentation.viewmodel.CryptoViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CryptoViewModel by viewModels {
        val container = (application as CryptoExchangeApp).container
        CryptoViewModel.factory(
            observeCryptoAssetsUseCase = container.observeCryptoAssetsUseCase,
            triggerManualSyncUseCase = container.triggerManualSyncUseCase,
            syncScheduler = container.syncScheduler
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CryptoExchangeTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                CryptoListScreen(
                    uiState = uiState,
                    onRefreshClick = viewModel::onRefreshClick,
                    onAssetClick = viewModel::onAssetClick,
                    onBackFromDetail = viewModel::onBackFromDetail,
                    onSearchQueryChange = viewModel::onSearchQueryChange
                )
            }
        }
    }
}
