// viewmodel/GalleryViewModel.kt
package com.example.imagedetection.viewmodel

import android.app.Application // Cần Application context cho ImageAnalyzer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.imagedetection.data.ImageItem
import com.example.imagedetection.data.ProcessedImage
import com.example.imagedetection.ml.ImageAnalyzer
import com.example.imagedetection.repository.ImageRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
// Import thêm nếu chưa có
import kotlin.random.Random

// Cần kế thừa AndroidViewModel để lấy Application Context
class GalleryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ImageRepository()
    // Khởi tạo ImageAnalyzer với applicationContext
    private val imageAnalyzer = ImageAnalyzer(application.applicationContext)

    // *** THAY ĐỔI 1: Danh sách các từ khóa ngẫu nhiên ***
    private val randomKeywords = listOf(
        "nature", "animals", "cats", "dogs", "flowers", "sky",
        "city", "travel", "food", "technology", "mountains", "beach",
        "space", "art", "music", "people", "cars", "ocean", "fruits", "gun", "girl"
        // Thêm các từ khóa khác bạn muốn ở đây
    )

    // *** THAY ĐỔI 2: Khởi tạo currentQuery với một từ khóa ngẫu nhiên ***
    // MutableStateFlow để giữ query hiện tại, bắt đầu với giá trị ngẫu nhiên
    private val currentQuery = MutableStateFlow(randomKeywords.random()) // Chọn ngẫu nhiên 1 từ khóa

    // Sử dụng flatMapLatest để khi query thay đổi, nó sẽ hủy flow cũ và tạo flow mới
    @OptIn(ExperimentalCoroutinesApi::class)
    val imageFlow: Flow<PagingData<ProcessedImage>> = currentQuery
        .flatMapLatest { query ->
            repository.getImageStream(query)
                .map { pagingData: PagingData<ImageItem> ->
                    pagingData.map { imageItem ->
                        val labels = imageAnalyzer.analyzeImage(imageItem.webformatURL)
                        ProcessedImage(
                            id = imageItem.id,
                            imageUrl = imageItem.webformatURL,
                            tags = imageItem.tags, // Bạn có thể hiển thị cả tags từ Pixabay
                            user = imageItem.user,
                            detectedLabels = labels
                        )
                    }
                }
        }
        .cachedIn(viewModelScope)

    // Hàm để thay đổi query tìm kiếm từ UI
    fun searchImages(query: String) {
        // *** THAY ĐỔI 3 (Tùy chọn): Nếu người dùng xóa trắng ô tìm kiếm,
        // bạn có thể chọn một từ khóa ngẫu nhiên khác thay vì mặc định là "fruits" ***
        currentQuery.value = query.trim().ifEmpty { randomKeywords.random() } // Chọn ngẫu nhiên khác nếu rỗng
        // Hoặc giữ lại fallback "fruits" nếu bạn muốn:
        // currentQuery.value = query.trim().ifEmpty { "fruits" }
    }
}