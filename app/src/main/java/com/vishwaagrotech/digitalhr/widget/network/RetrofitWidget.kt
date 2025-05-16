package com.vishwaagrotech.digitalhr.widget.network

import com.vishwaagrotech.digitalhr.repository.network.api.attentancestatus.AttendanceStatusResponse
import com.vishwaagrotech.digitalhr.utils.Constant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


/**
 *Copyright (c) 2022, All Rights Reserved.
 */
public interface RetrofitWidget {

    @POST("employees/check-in")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    suspend fun getCheckInResponse(
        @Header("Authorization") token: String,
        @Field("router_bssid") bssid: String,
        @Field("check_in_latitude") latitude: Double,
        @Field("check_in_longitude") longitude: Double
    ): Response<AttendanceStatusResponse>

    @POST("employees/check-out")
    @Headers("Accept: application/json")
    @FormUrlEncoded
    suspend fun getCheckOutResponse(
        @Header("Authorization") token: String,
        @Field("router_bssid") bssid: String,
        @Field("check_out_latitude") latitude: Double,
        @Field("check_out_longitude") longitude: Double
    ): Response<AttendanceStatusResponse>

    companion object {

        var retrofitService: RetrofitWidget? = null
        private const val BASE_URL: String = "${Constant.APP_URL}api/"

        fun getInstance(): RetrofitWidget {

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
                retrofitService = retrofit.create(RetrofitWidget::class.java)
            }
            return retrofitService!!
        }
    }
}