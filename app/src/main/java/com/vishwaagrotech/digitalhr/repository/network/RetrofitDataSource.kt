package com.vishwaagrotech.digitalhr.repository.network

import com.vishwaagrotech.digitalhr.utils.Constant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject


class RetrofitDataSource @Inject constructor() {

    var retrofitService: RetrofitService? = null

    fun getInstance(): RetrofitService {
        if (retrofitService == null) {
            val logging = HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            retrofitService = retrofit.create(RetrofitService::class.java)
        }

        return retrofitService!!
    }

    companion object {
        private const val BASE_URL: String = "${Constant.APP_URL}api/"
    }
}