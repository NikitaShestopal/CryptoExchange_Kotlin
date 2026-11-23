package com.example.cryptoexchange.data.repository

import com.example.cryptoexchange.data.local.CryptoDao
import com.example.cryptoexchange.data.mapper.toDomain
import com.example.cryptoexchange.data.mapper.toEntity
import com.example.cryptoexchange.data.remote.CoinGeckoApi
import com.example.cryptoexchange.domain.model.CryptoAsset
import com.example.cryptoexchange.domain.model.SyncResult
import com.example.cryptoexchange.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class CryptoRepositoryImpl(
    private val api: CoinGeckoApi,
    private val dao: CryptoDao
) : CryptoRepository {

    override fun observeTopAssets(): Flow<List<CryptoAsset>> {
        return dao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncTopAssets(): SyncResult {
        return try {
            val response = api.getTopAssets()
            when {
                response.isSuccessful -> {
                    val rawList = response.body()
                    if (rawList.isNullOrEmpty()) {
                        return SyncResult.UnknownError("API повернув порожній список криптовалют")
                    }

                    val syncedAt = System.currentTimeMillis()
                    val entities = rawList.mapIndexedNotNull { index, dto ->
                        dto.toEntity(fallbackRank = index + 1, updatedAtMillis = syncedAt)
                    }

                    if (entities.isEmpty()) {
                        return SyncResult.UnknownError("Не вдалося розпарсити жодного активу")
                    }

                    dao.replaceAll(entities)
                    SyncResult.Success
                }

                response.code() == 401 || response.code() == 403 -> {
                    SyncResult.AuthError("Помилка авторизації CoinGecko API (${response.code()}). Перевірте COINGECKO_API_KEY.")
                }

                response.code() == 429 -> {
                    val retryAfter = response.headers()["Retry-After"]?.toLongOrNull()
                    SyncResult.RateLimitError(
                        message = "Перевищено ліміт запитів CoinGecko API (429). Спробуйте пізніше.",
                        retryAfterSeconds = retryAfter
                    )
                }

                else -> {
                    SyncResult.UnknownError("Помилка сервера: HTTP ${response.code()}")
                }
            }
        } catch (e: IOException) {
            SyncResult.NetworkError("Немає з'єднання з сервером: ${e.localizedMessage ?: "помилка мережі"}")
        } catch (e: Exception) {
            SyncResult.UnknownError("Неочікувана помилка: ${e.localizedMessage ?: e.javaClass.simpleName}")
        }
    }
}
