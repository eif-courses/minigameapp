package eif.viko.lt.minigameapp.root.core.di

import eif.viko.lt.minigameapp.root.core.data.storage.SecureTokenStorage
import eif.viko.lt.minigameapp.root.core.data.storage.TokenStorage
import org.koin.dsl.module

val coreModule = module {

    single<TokenStorage> {
        SecureTokenStorage(context = get())
    }
}
