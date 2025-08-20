package eif.viko.lt.minigameapp.root.auth.data.remote


import eif.viko.lt.minigameapp.root.auth.domain.utils.TokenStorage
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenStorage: TokenStorage
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth for login/register endpoints
        if (originalRequest.url.encodedPath.contains("/api/v1/auth/login") ||
            originalRequest.url.encodedPath.contains("/api/v1/auth/register")) {
            return chain.proceed(originalRequest)
        }

        return runBlocking {
            val token = tokenStorage.getToken()
            val newRequest = if (token != null) {
                originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
            } else {
                originalRequest
            }
            chain.proceed(newRequest)
        }
    }
}