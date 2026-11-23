package com.example.cryptoexchange.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CryptoDao {

    @Query("SELECT * FROM crypto_assets ORDER BY rank ASC")
    fun observeAll(): Flow<List<CryptoAssetEntity>>

    @Query("DELETE FROM crypto_assets")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(assets: List<CryptoAssetEntity>)

    @Transaction
    suspend fun replaceAll(assets: List<CryptoAssetEntity>) {
        clearAll()
        insertAll(assets)
    }
}
