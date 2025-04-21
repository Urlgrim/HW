// api/RetrofitInstance.kt
package com.example.imagedetection.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: PixabayApiService by lazy { // 'lazy' để chỉ khởi tạo khi cần
        Retrofit.Builder()
            .baseUrl(PixabayApiService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Dùng Gson để parse JSON
            .build()
            .create(PixabayApiService::class.java) // Tạo instance của interface
    }
}