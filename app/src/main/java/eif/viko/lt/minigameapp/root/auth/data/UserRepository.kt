package eif.viko.lt.minigameapp.root.auth.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.dsl.module

class UserRepository(private val apiService: ApiService) {

    suspend fun getUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUsers()
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUser(id: Int): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUser(id)
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUser(user: User): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.createUser(user)
            Result.success(response.data)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Add to Koin module
val repositoryModule = module {
    single { UserRepository(get()) }
}