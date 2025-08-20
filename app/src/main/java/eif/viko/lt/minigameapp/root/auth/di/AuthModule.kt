package eif.viko.lt.minigameapp.root.auth.di

import android.app.Application
import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import eif.viko.lt.minigameapp.root.BuildConfig
import eif.viko.lt.minigameapp.root.auth.data.remote.AuthApi
import eif.viko.lt.minigameapp.root.auth.data.remote.AuthInterceptor
import eif.viko.lt.minigameapp.root.auth.data.remote.AuthRepositoryImpl
import eif.viko.lt.minigameapp.root.auth.domain.repository.AuthRepository
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.CheckAuthStatusUseCase
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignInUseCase
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignInWithBattleNetUseCase
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignOutUseCase
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignUpUseCase
import eif.viko.lt.minigameapp.root.auth.domain.utils.SecureTokenStorage
import eif.viko.lt.minigameapp.root.auth.domain.utils.TokenStorage
import eif.viko.lt.minigameapp.root.auth.presentation.viewmodel.AuthStateViewModel
import eif.viko.lt.minigameapp.root.auth.presentation.viewmodel.SignInViewModel
import eif.viko.lt.minigameapp.root.auth.presentation.viewmodel.SignUpViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import org.koin.core.module.dsl.viewModelOf // Add this import

val authModule = module {

    // Tokenu saugykla
    single<TokenStorage> {
        SecureTokenStorage(context = get())
    }

//    single<TokenStorage> {
//        object : TokenStorage {
//            private val prefs = get<Application>().getSharedPreferences("debug_auth", Context.MODE_PRIVATE)
//
//            override suspend fun saveToken(token: String) {
//                println("💾 DEBUG: Saving token: ${token.take(20)}...")
//                val success = prefs.edit().putString("auth_token", token).commit()
//                println("💾 DEBUG: Save result = $success")
//
//                // Verify immediately
//                val saved = prefs.getString("auth_token", null)
//                println("💾 DEBUG: Immediate verification: ${saved?.take(20)}...")
//            }
//
//            override suspend fun getToken(): String? {
//                val token = prefs.getString("auth_token", null)
//                println("🔍 DEBUG: Retrieved token: ${token?.take(20)}...")
//                return token
//            }
//
//            override suspend fun clearToken() {
//                prefs.edit().remove("auth_token").commit()
//                println("🧹 DEBUG: Token cleared")
//            }
//
//            override suspend fun hasValidToken(): Boolean {
//                val token = getToken()
//                val isValid = token != null && token.isNotBlank()
//                println("🔍 DEBUG: hasValidToken = $isValid")
//                return isValid
//            }
//        }
//    }

    // Tokeno iterpimui i http requestus
    single<AuthInterceptor>{
        AuthInterceptor(tokenStorage = get())
    }

    /* Moshi JSON converter */
    single<Moshi> {
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    /* OkHttp Client */
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

    /* Retrofit instance */
    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://minigameapi-production.up.railway.app/")
            .client(get<OkHttpClient>())
            .addConverterFactory(MoshiConverterFactory.create(get<Moshi>()))
            .build()
    }

    /* API Service */
    single<AuthApi> {
        get<Retrofit>().create(AuthApi::class.java)
    }

    /* Repository */

    single<AuthRepository> {
        AuthRepositoryImpl(
            api = get(),
            tokenStorage = get(),
            context = get()
        )
    }

    factoryOf(::SignInUseCase)
    factoryOf(::SignInWithBattleNetUseCase)
    factoryOf(::CheckAuthStatusUseCase)
    factoryOf(::SignUpUseCase)
    factoryOf(::SignOutUseCase)

    viewModelOf(::SignInViewModel)      // Replace viewModel<SignInViewModel> { SignInViewModel(...) }
    viewModelOf(::SignUpViewModel)      // Replace viewModel<SignUpViewModel> { SignUpViewModel(...) }
    viewModelOf(::AuthStateViewModel)   // Replace viewModel<AuthStateViewModel> { AuthStateViewModel(...) }

}