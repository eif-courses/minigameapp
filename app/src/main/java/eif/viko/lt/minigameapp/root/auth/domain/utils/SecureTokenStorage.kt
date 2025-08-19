package eif.viko.lt.minigameapp.root.auth.domain.utils

// In data/local or data/storage

import android.content.Context
import android.util.Base64
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONObject

interface TokenStorage {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
    suspend fun hasValidToken(): Boolean
}

class SecureTokenStorage(
    private val context: Context
) : TokenStorage {

    private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

    private val TOKEN_KEY = stringPreferencesKey("auth_token")

    // Configure Tink AEAD
    private val aead: Aead by lazy {
        AeadConfig.register()
        val keysetHandle: KeysetHandle = AndroidKeysetManager.Builder()
            .withSharedPref(context, "master_keyset", "master_prefs")
            .withKeyTemplate(com.google.crypto.tink.aead.AesGcmKeyManager.aes256GcmTemplate())
            .withMasterKeyUri("android-keystore://master_key")
            .build()
            .keysetHandle
        keysetHandle.getPrimitive(Aead::class.java)
    }

    private fun encrypt(plainText: String): String {
        val encrypted = aead.encrypt(
            plainText.toByteArray(),
            null // optional "associated data" (AAD)
        )
        return Base64.encodeToString(encrypted, Base64.NO_WRAP)
    }

    private fun decrypt(cipherText: String): String {
        val decrypted = aead.decrypt(
            Base64.decode(cipherText, Base64.NO_WRAP),
            null
        )
        return String(decrypted)
    }

    override suspend fun saveToken(token: String): Unit = withContext(Dispatchers.IO) {
        val encrypted = encrypt(token)
        context.dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = encrypted
        }
    }

    override suspend fun getToken(): String? = withContext(Dispatchers.IO) {
        context.dataStore.data.map { prefs ->
            prefs[TOKEN_KEY]?.let { decrypt(it) }
        }.first()
    }

    override suspend fun clearToken(): Unit = withContext(Dispatchers.IO) {
        context.dataStore.edit { prefs ->
            prefs.remove(TOKEN_KEY)
        }
    }

    override suspend fun hasValidToken(): Boolean {
        val token = getToken()
        return token != null && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val parts = token.split(".")
            if (parts.size != 3) return true
            val payloadJson = String(
                Base64.decode(parts[1], Base64.URL_SAFE)
            )
            val exp = JSONObject(payloadJson).optLong("exp", 0)
            val now = System.currentTimeMillis() / 1000
            exp < now
        } catch (e: Exception) {
            true // invalid = expired
        }
    }
}
