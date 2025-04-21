// repository/ImageRepository.kt
package com.example.imagedetection.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.imagedetection.api.PixabayApiService
import com.example.imagedetection.api.RetrofitInstance
import com.example.imagedetection.data.ImageItem
// *** KHÔNG CẦN IMPORT HẰNG SỐ PAGE SIZE TỪ PAGING NỮA ***
// import com.example.imagedetection.paging.NETWORK_PAGE_SIZE // Hoặc TEST_PAGE_SIZE
import com.example.imagedetection.paging.PixabayPagingSource
import kotlinx.coroutines.flow.Flow

class ImageRepository(
    private val apiService: PixabayApiService = RetrofitInstance.api
) {
    fun getImageStream(query: String): Flow<PagingData<ImageItem>> {
        return Pager(
            config = PagingConfig(
                // *** THAY ĐỔI: Đặt pageSize trực tiếp thành 6 ***
                pageSize = 10,
                enablePlaceholders = false
                // initialLoadSize cũng có thể đặt là 6 nếu muốn, nhưng không bắt buộc
                // vì PagingSource đã kiểm soát số lượng load.
                // initialLoadSize = 6
            ),
            // Cung cấp factory để tạo PagingSource mới mỗi khi cần refresh
            pagingSourceFactory = { PixabayPagingSource(query, apiService) }
        ).flow // Trả về Flow<PagingData<ImageItem>>
    }
}