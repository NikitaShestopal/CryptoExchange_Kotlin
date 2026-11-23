package com.example.cryptoexchange.data.repository

import com.example.cryptoexchange.data.local.CryptoAssetEntity
import com.example.cryptoexchange.data.local.CryptoDao
import com.example.cryptoexchange.data.remote.CoinGeckoApi
import com.example.cryptoexchange.data.remote.dto.CoinGeckoAssetDto
import com.example.cryptoexchange.domain.model.SyncResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class CryptoRepositoryImplTest {

    @Test
    fun `preserves cached data when network request throws IOException`() = runTest {
        val dao = FakeCryptoDao(
            initial = listOf(
                CryptoAssetEntity("bitcoin", 1, "Bitcoin", "BTC", null, 60000.0, 1000L)
            )
        )
        val api = FakeCoinGeckoApi { throw IOException("No internet") }
        val repo = CryptoRepositoryImpl(api = api, dao = dao)

        val result = repo.syncTopAssets()

        assertTrue(result is SyncResult.NetworkError)
        val stored = repo.observeTopAssets().first()
        assertEquals(1, stored.size)
        assertEquals("bitcoin", stored.first().id)
        assertEquals(1000L, stored.first().updatedAtMillis)
    }

    @Test
    fun `replaces old top list so dropped assets do not accumulate`() = runTest {
        val dao = FakeCryptoDao(
            initial = listOf(
                CryptoAssetEntity("old-coin", 1, "OldCoin", "OLD", null, 1.0, 500L)
            )
        )
        val api = FakeCoinGeckoApi {
            Response.success(
                listOf(
                    CoinGeckoAssetDto("bitcoin", "btc", "Bitcoin", null, 65000.0, 1),
                    CoinGeckoAssetDto("ethereum", "eth", "Ethereum", null, 2200.0, 2)
                )
            )
        }
        val repo = CryptoRepositoryImpl(api = api, dao = dao)

        val result = repo.syncTopAssets()

        assertEquals(SyncResult.Success, result)
        val stored = repo.observeTopAssets().first()
        assertEquals(listOf("bitcoin", "ethereum"), stored.map { it.id })
    }

    @Test
    fun `returns AuthError on 401 and keeps existing database intact`() = runTest {
        val dao = FakeCryptoDao(
            initial = listOf(
                CryptoAssetEntity("bitcoin", 1, "Bitcoin", "BTC", null, 60000.0, 1000L)
            )
        )
        val api = FakeCoinGeckoApi {
            Response.error(401, "Unauthorized".toResponseBody("text/plain".toMediaType()))
        }
        val repo = CryptoRepositoryImpl(api = api, dao = dao)

        val result = repo.syncTopAssets()

        assertTrue(result is SyncResult.AuthError)
        assertEquals(1, repo.observeTopAssets().first().size)
    }

    private class FakeCryptoDao(initial: List<CryptoAssetEntity> = emptyList()) : CryptoDao {
        private val state = MutableStateFlow(initial)

        override fun observeAll(): Flow<List<CryptoAssetEntity>> = state

        override suspend fun clearAll() {
            state.value = emptyList()
        }

        override suspend fun insertAll(assets: List<CryptoAssetEntity>) {
            state.value = assets.sortedBy { it.rank }
        }

        override suspend fun replaceAll(assets: List<CryptoAssetEntity>) {
            state.value = assets.sortedBy { it.rank }
        }
    }

    private class FakeCoinGeckoApi(
        private val block: suspend () -> Response<List<CoinGeckoAssetDto>>
    ) : CoinGeckoApi {
        override suspend fun getTopAssets(
            vsCurrency: String,
            order: String,
            perPage: Int,
            page: Int
        ): Response<List<CoinGeckoAssetDto>> = block()
    }
}
