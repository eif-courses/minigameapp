package eif.viko.lt.minigameapp.root.auth.di


import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module




val authModule = module {



    // ViewModel
//    viewModel<LoginViewModel> {
//        LoginViewModel(
//            userRepository = get()
//        )
//    }
}


//val authModule = module {



    /* Moshi JSON converter */
//    single<Moshi> {
//        Moshi.Builder()
//            .add(KotlinJsonAdapterFactory())
//            .build()
//    }
//
//    // OkHttp Client with logging
//    single<OkHttpClient> {
//        OkHttpClient.Builder()
//            .addInterceptor(HttpLoggingInterceptor().apply {
//                level = if (BuildConfig.DEBUG) {
//                    HttpLoggingInterceptor.Level.BODY
//                } else {
//                    HttpLoggingInterceptor.Level.NONE
//                }
//            })
//            .connectTimeout(30, TimeUnit.SECONDS)
//            .readTimeout(30, TimeUnit.SECONDS)
//            .writeTimeout(30, TimeUnit.SECONDS)
//            .build()
//    }
//
//    // Retrofit instance - FIXED: Add explicit type
//    single<Retrofit> {
//        Retrofit.Builder()
//            .baseUrl("https://your-api-base-url.com/api/v1/")
//            .client(get<OkHttpClient>())
//            .addConverterFactory(MoshiConverterFactory.create(get<Moshi>()))
//            .build()
//    }
//
//    // API Service - FIXED: Use get<Retrofit>() instead of get<>()
//    single<ApiService> {
//        get<Retrofit>().create(ApiService::class.java)
//    }
//
//}