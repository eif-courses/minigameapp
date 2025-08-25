package eif.viko.lt.minigameapp.root.auth.di

import eif.viko.lt.minigameapp.root.auth.data.remote.AuthApi
import eif.viko.lt.minigameapp.root.auth.data.remote.AuthRepositoryImpl
import eif.viko.lt.minigameapp.root.auth.domain.repository.AuthRepository
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.CheckAuthStatusUseCase
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignInUseCase
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignInWithBattleNetUseCase
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignOutUseCase
import eif.viko.lt.minigameapp.root.auth.domain.use_cases.SignUpUseCase
import eif.viko.lt.minigameapp.root.auth.presentation.viewmodel.AuthStateViewModel
import eif.viko.lt.minigameapp.root.auth.presentation.viewmodel.SignInViewModel
import eif.viko.lt.minigameapp.root.auth.presentation.viewmodel.SignUpViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module
import retrofit2.Retrofit
import org.koin.core.module.dsl.viewModelOf

val authModule = module {

    // Tokenu saugykla


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