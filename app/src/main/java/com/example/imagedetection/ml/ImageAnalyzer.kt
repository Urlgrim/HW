// ml/ImageAnalyzer.kt
package com.example.imagedetection.ml

import android.content.Context
import android.graphics.Bitmap
import coil.imageLoader
import coil.request.ImageRequest
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await

class ImageAnalyzer(
    private val context: Context,
    // Không cần CoroutineScope ở đây nếu bạn gọi analyzeImage từ một coroutine khác (như viewModelScope)
) {
    // Khởi tạo labeler chỉ một lần
    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    suspend fun analyzeImage(imageUrl: String): List<String> {
        return try {
            // 1. Dùng Coil để tải ảnh về dạng Bitmap
            val imageLoader = context.imageLoader
            val request = ImageRequest.Builder(context)
                .data(imageUrl)
                .allowHardware(false) // Quan trọng: ML Kit thường cần software bitmap
                .target { drawable ->
                    // Callback này có thể không được gọi nếu dùng .execute() trực tiếp
                    // Thay vào đó, dùng execute() và lấy bitmap từ ImageResult
                }
                .build()

            // Thực thi request và lấy bitmap
            val result = imageLoader.execute(request)
            val bitmap = (result.drawable as? android.graphics.drawable.BitmapDrawable)?.bitmap

            if (bitmap == null) {
                println("Lỗi: Không thể lấy Bitmap từ URL: $imageUrl")
                return emptyList() // Trả về list rỗng nếu không có bitmap
            }

            // 2. Tạo InputImage từ Bitmap cho ML Kit
            // Tham số rotationDegrees là 0 nếu ảnh đã đúng chiều
            val image = InputImage.fromBitmap(bitmap, 0)

            // 3. Process ảnh bằng labeler (dùng await() để chạy bất đồng bộ với coroutine)
            val labels = labeler.process(image).await() // await() từ kotlinx-coroutines-play-services

            // 4. Lấy tối đa 3 nhãn đầu tiên và chỉ lấy phần text
            labels.take(3).map { it.text }

        } catch (e: Exception) {
            // 5. Xử lý lỗi (ví dụ: lỗi mạng khi tải ảnh, lỗi ML Kit)
            println("Lỗi khi phân tích ảnh $imageUrl: ${e.message}")
            e.printStackTrace()
            emptyList() // Trả về list rỗng nếu có lỗi
        }
    }
}