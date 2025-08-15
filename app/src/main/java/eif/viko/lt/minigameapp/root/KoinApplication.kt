package eif.viko.lt.minigameapp.root

import android.app.Application
import eif.viko.lt.minigameapp.root.auth.data.repositoryModule
import eif.viko.lt.minigameapp.root.auth.di.authModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class KoinApplication : Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@KoinApplication)
            modules(
                authModule,
                repositoryModule
            ) // Pridėti modulius iš DI package
        }
    }
}