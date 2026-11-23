package com.example.cryptoexchange.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [CryptoAssetEntity::class],
    version = 2,
    exportSchema = false
)
abstract class CryptoDatabase : RoomDatabase() {
    abstract fun cryptoDao(): CryptoDao

    companion object {
        fun build(context: Context): CryptoDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                CryptoDatabase::class.java,
                "crypto_exchange.db"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
