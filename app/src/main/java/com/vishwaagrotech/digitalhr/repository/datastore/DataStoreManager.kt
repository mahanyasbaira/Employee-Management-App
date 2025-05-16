package com.vishwaagrotech.digitalhr.repository.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vishwaagrotech.digitalhr.model.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(
    name = "cninfotech_prefs"
)

class DataStoreManager @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        val USER_LOGGED_IN_KEY = booleanPreferencesKey("USER_LOGGED_IN")
        val USER_SECURITY_KEY = booleanPreferencesKey("USER_SECURITY")
        val USER_TOKEN = stringPreferencesKey("USER_TOKEN")
        val USER_FCM_TOKEN = stringPreferencesKey("USER_FCM_TOKEN")
        val USER_ID = stringPreferencesKey("USER_ID")
        val USER_NAME = stringPreferencesKey("USER_NAME")
        val USER_FULL_NAME = stringPreferencesKey("USER_FULL_NAME")
        val USER_ACTIVE_STATUS = booleanPreferencesKey("USER_ACTIVE_STATUS")
        val USER_EMAIL = stringPreferencesKey("USER_EMAIL")
        val USER_IMAGE = stringPreferencesKey("USER_IMAGE")
        val REMEMBER_ME = stringPreferencesKey("REMEMBER_ME")
    }

    //store value in datastore

    suspend fun storeLoggedIn(boolean: Boolean) {
        context.dataStore.edit {
            it[USER_LOGGED_IN_KEY] = boolean
        }
    }

    suspend fun storeSecurityCheck(boolean: Boolean) {
        context.dataStore.edit {
            it[USER_SECURITY_KEY] = boolean
        }
    }

    suspend fun storeRememberMe(string: String) {
        context.dataStore.edit {
            it[REMEMBER_ME] = string
        }
    }

    suspend fun storeUsername(string: String) {
        context.dataStore.edit {
            it[USER_NAME] = string
        }
    }

    suspend fun storeUserId(string: String) {
        context.dataStore.edit {
            it[USER_ID] = string
        }
    }

    suspend fun storeUserEmail(string: String) {
        context.dataStore.edit {
            it[USER_EMAIL] = string
        }
    }

    suspend fun storeUserFullname(string: String) {
        context.dataStore.edit {
            it[USER_FULL_NAME] = string
        }
    }

    suspend fun storeToken(string: String) {
        context.dataStore.edit {
            it[USER_TOKEN] = string
        }
    }

    suspend fun storeFCMToken(string: String) {
        context.dataStore.edit {
            it[USER_FCM_TOKEN] = string
        }
    }

    suspend fun storeImage(string: String) {
        context.dataStore.edit {
            it[USER_IMAGE] = string
        }
    }

    suspend fun storeActiveStatus(boolean: Boolean) {
        context.dataStore.edit {
            it[USER_ACTIVE_STATUS] = boolean
        }
    }

    suspend fun storeUser(user: com.vishwaagrotech.digitalhr.repository.network.api.login.User) {
        context.dataStore.edit {
            it[USER_ID] = user.id.toString()
            it[USER_FULL_NAME] = user.name
            it[USER_IMAGE] = user.avatar
            it[USER_EMAIL] = user.email
            it[USER_NAME] = user.username
            it[USER_ACTIVE_STATUS] = user.active_status
        }
    }

    //retrieve value from datastore

    fun getLoggedIn() = context.dataStore.data.map {
        it[USER_LOGGED_IN_KEY] ?: false
    }

    fun getSecurityCheck() = context.dataStore.data.map {
        it[USER_SECURITY_KEY] ?: false
    }

    fun getActiveStatus() = context.dataStore.data.map {
        it[USER_ACTIVE_STATUS] ?: false
    }

    fun getToken(): Flow<String> = context.dataStore.data.map {
        it[USER_TOKEN] ?: ""
    }

    fun getFCMToken() = context.dataStore.data.map {
        it[USER_FCM_TOKEN] ?: ""
    }

    fun getRememberMe() = context.dataStore.data.map {
        it[REMEMBER_ME] ?: ""
    }

    fun getUserEmail() = context.dataStore.data.map {
        it[USER_EMAIL] ?: ""
    }

    fun getUserFullName() = context.dataStore.data.map {
        it[USER_FULL_NAME] ?: ""
    }

    fun getUsername() = context.dataStore.data.map {
        it[USER_NAME] ?: ""
    }

    fun getUserImage() = context.dataStore.data.map {
        it[USER_IMAGE] ?: ""
    }

    fun getUser() = context.dataStore.data.map {
        User(
            it[USER_ID]?.toInt() ?: 0,
            it[USER_NAME] ?: "",
            it[USER_FULL_NAME] ?: "",
            "",
            it[USER_IMAGE] ?: "",
            true,
        )
    }

    //clear all data

    suspend fun clearAllData() {
        context.dataStore.edit {
            it[USER_IMAGE] = ""
            it[USER_LOGGED_IN_KEY] = false
            it[USER_SECURITY_KEY] = false
            it[USER_TOKEN] = ""
            it[USER_EMAIL] = ""
            it[USER_FULL_NAME] = ""
            it[USER_NAME] = ""
            it[USER_ACTIVE_STATUS] = false
        }
    }

}