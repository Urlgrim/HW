// api/PixabayApiService.kt
package com.example.imagedetection.api

import com.example.imagedetection.BuildConfig
import com.example.imagedetection.data.PixabayResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface PixabayApiService {

    companion object {
        const val BASE_URL = "https://pixabay.com/api/"
        const val API_KEY = BuildConfig.PIXABAY_API_KEY // Lấy key từ BuildConfig
    }

    @GET(".") // Gọi đến endpoint gốc của base url
    suspend fun searchImages(
        @Query("key") apiKey: String = API_KEY,
        @Query("q") query: String, // Từ khóa tìm kiếm (ví dụ: "dogs")
        @Query("image_type") imageType: String = "photo",
        @Query("page") page: Int, // Số trang
        @Query("per_page") perPage: Int // Số ảnh mỗi trang
    ): PixabayResponse // Kiểu trả về là PixabayResponse đã định nghĩa
}