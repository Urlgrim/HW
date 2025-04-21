// paging/PixabayPagingSource.kt
package com.example.imagedetection.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.imagedetection.api.PixabayApiService
// Bỏ import RetrofitInstance nếu ApiService được inject qua constructor
// import com.example.imagedetection.api.RetrofitInstance
import com.example.imagedetection.data.ImageItem
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1
// *** Đặt số lượng ảnh mong muốn cho test ***
private const val TEST_PAGE_SIZE = 10
// Đổi tên hoặc comment out hằng số cũ để tránh nhầm lẫn
// const val NETWORK_PAGE_SIZE = 6

class PixabayPagingSource(
    private val query: String, // Từ khóa tìm kiếm
    private val apiService: PixabayApiService // Nhận ApiService qua constructor
) : PagingSource<Int, ImageItem>() { // Key là Int (số trang), Value là ImageItem

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ImageItem> {
        // Xác định trang cần load
        val page = params.key ?: STARTING_PAGE_INDEX

        // *** THAY ĐỔI QUAN TRỌNG: Chỉ load trang đầu tiên ***
        if (page > STARTING_PAGE_INDEX) {
            // Nếu Paging cố gắng yêu cầu trang > 1, trả về kết quả rỗng và dừng lại
            return LoadResult.Page(emptyList(), prevKey = null, nextKey = null)
        }

        return try {
            // Gọi API để lấy ảnh
            val response = apiService.searchImages(
                query = query,
                // *** THAY ĐỔI: Luôn yêu cầu trang đầu tiên ***
                page = STARTING_PAGE_INDEX,
                // *** THAY ĐỔI: Luôn yêu cầu số lượng ảnh cố định là TEST_PAGE_SIZE ***
                perPage = TEST_PAGE_SIZE // Bỏ qua params.loadSize
            )
            // Nên kiểm tra null cho hits
            val images = response.hits ?: emptyList()

            // Trả về LoadResult.Page thành công
            LoadResult.Page(
                data = images,
                prevKey = null, // Trang đầu tiên không có trang trước
                // *** THAY ĐỔI QUAN TRỌNG: Đặt nextKey = null để không load thêm ***
                nextKey = null
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ImageItem>): Int? {
        // Logic này không còn quá quan trọng khi chỉ có 1 trang
        // Có thể trả về null hoặc trang bắt đầu
        return null
        // hoặc: return STARTING_PAGE_INDEX
        /*
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
        */
    }
}