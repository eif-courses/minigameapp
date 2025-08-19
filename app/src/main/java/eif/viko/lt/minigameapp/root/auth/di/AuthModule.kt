package eif.viko.lt.minigameapp.root.auth.di

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
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import org.koin.core.module.dsl.viewModelOf // Add this import

val authModule = module {

    // Tokenu saugykla
    single<TokenStorage> {
        SecureTokenStorage(context = androidContext())
    }

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
            tokenStorage = get()
        )
    }

    /* Use Cases */
    single<SignInUseCase> {
        SignInUseCase(
            repository = get()
        )
    }
    single<CheckAuthStatusUseCase>{
        CheckAuthStatusUseCase(repository = get())
    }
    single<SignOutUseCase>{
        SignOutUseCase(repository = get())
    }

    single<SignUpUseCase>{
        SignUpUseCase(repository = get())
    }
    single <SignInWithBattleNetUseCase>{
        SignInWithBattleNetUseCase(repository = get())
    }

    viewModelOf(::SignInViewModel)      // Replace viewModel<SignInViewModel> { SignInViewModel(...) }
    viewModelOf(::SignUpViewModel)      // Replace viewModel<SignUpViewModel> { SignUpViewModel(...) }
    viewModelOf(::AuthStateViewModel)   // Replace viewModel<AuthStateViewModel> { AuthStateViewModel(...) }

}