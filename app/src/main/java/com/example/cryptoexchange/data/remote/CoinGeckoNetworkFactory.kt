package com.example.cryptoexchange.data.remote

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object CoinGeckoNetworkFactory {

    fun createApi(apiKey: String, isDebug: Boolean = false): CoinGeckoApi {
        val authInterceptor = Interceptor { chain ->
            val requestBuilder = chain.request().newBuilder()
                .addHeader("Accept", "application/json")
            if (apiKey.isNotBlank()) {
                requestBuilder.addHeader("x-cg-demo-api-key", apiKey)
            }
            chain.proceed(requestBuilder.build())
        }

        val clientBuilder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)

        if (isDebug) {
            val logging = HttpLoggingInterceptor().apply {
                // BASIC не виводить заголовки, щоб API-ключ не потрапляв у логи
                level = HttpLoggingInterceptor.Level.BASIC
            }
            clientBuilder.addInterceptor(logging)
        }

        return Retrofit.Builder()
            .baseUrl(CoinGeckoApi.BASE_URL)
            .client(clientBuilder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinGeckoApi::class.java)
    }
}
