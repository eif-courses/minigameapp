package eif.viko.lt.minigameapp.root.core.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import eif.viko.lt.minigameapp.root.BuildConfig
import eif.viko.lt.minigameapp.root.core.data.network.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {

    // AuthInterceptor - used across all features
    single<AuthInterceptor> {
        AuthInterceptor(tokenStorage = get())
    }

    // Moshi JSON converter - used across all features
    single<Moshi> {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    // OkHttp Client - used across all features
    single<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthInterceptor>())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    // Base Retrofit instance - used across all features
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://minigameapi-production.up.railway.app/")
            .client(get<OkHttpClient>())
            .addConverterFactory(MoshiConverterFactory.create(get<Moshi>()))
            .build()
    }
}