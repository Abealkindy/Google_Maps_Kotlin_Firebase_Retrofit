package com.abraham24.kotlinmvpmapsretrofit.Init

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by KOCHOR on 8/27/2017.
 */
class InitRetrofit {
    fun getInit(): Retrofit {
        return Retrofit.Builder().baseUrl("https://maps.googleapis.com/maps/api/directions/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }
    fun getInitInstance(): ApiService {
        return  getInit().create(ApiService::class.java)
    }
}

interface ApiService {
    @GET("json")
    fun request_route(
            @Query("origin") awal : String,
            @Query("destination") tujuan : String,
            @Query("mode") mode : String
    ): Call<ResponseJSON>
}