package com.vishwaagrotech.digitalhr.repository

import com.vishwaagrotech.digitalhr.repository.datastore.DataStoreManager
import com.vishwaagrotech.digitalhr.repository.network.RetrofitService
import com.vishwaagrotech.digitalhr.repository.network.api.login.LoginResponse
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val retrofitService: RetrofitService,
    val dataStoreManager: DataStoreManager
) {

    suspend fun getLoginResponse(
        email: String,
        password: String,
        device: String,
        uuid: String,
        fcm: String
    ) =
        retrofitService.getLoginResponse(email, password, fcm, device, uuid)

    suspend fun storeLoginUserDetail(user: LoginResponse) {
        dataStoreManager.storeImage(user.data.user.avatar)
        dataStoreManager.storeToken(user.data.tokens)
        dataStoreManager.storeUserEmail(user.data.user.email)
        dataStoreManager.storeUsername(user.data.user.username)
        dataStoreManager.storeUserFullname(user.data.user.name)
        dataStoreManager.storeLoggedIn(true)
    }

    suspend fun getRememberMe() = dataStoreManager.getRememberMe().first()

    suspend fun storeRememberMe(value : String) = dataStoreManager.storeRememberMe(value)
}