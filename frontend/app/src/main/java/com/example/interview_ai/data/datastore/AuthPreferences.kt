package com.example.interview_ai.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

class AuthPreferences(private val context: Context) {

    companion object {
        private val JWT_TOKEN_KEY    = stringPreferencesKey("jwt_token")
        private val USER_NAME_KEY    = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY   = stringPreferencesKey("user_email")
        private val TARGET_ROLE_KEY  = stringPreferencesKey("target_role")
    }

    val jwtToken: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[JWT_TOKEN_KEY]
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME_KEY] ?: ""
    }

    val userEmail: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_EMAIL_KEY] ?: ""
    }

    val targetRole: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[TARGET_ROLE_KEY] ?: "Android Developer"
    }

    suspend fun saveAuthSession(token: String, name: String, email: String, role: String) {
        context.dataStore.edit { prefs ->
            prefs[JWT_TOKEN_KEY]   = token
            prefs[USER_NAME_KEY]   = name
            prefs[USER_EMAIL_KEY]  = email
            prefs[TARGET_ROLE_KEY] = role
        }
    }

    suspend fun saveTargetRole(role: String) {
        context.dataStore.edit { prefs ->
            prefs[TARGET_ROLE_KEY] = role
        }
    }

    suspend fun clearAuthSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(JWT_TOKEN_KEY)
            prefs.remove(USER_NAME_KEY)
            prefs.remove(USER_EMAIL_KEY)
            prefs.remove(TARGET_ROLE_KEY)
        }
    }
}
