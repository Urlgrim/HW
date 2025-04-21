// adapter/ImageAdapter.kt
package com.example.imagedetection.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.imagedetection.R
import com.example.imagedetection.data.ProcessedImage
import com.example.imagedetection.databinding.ItemImageBinding // Import ViewBinding class

class ImageAdapter : PagingDataAdapter<ProcessedImage, ImageAdapter.ImageViewHolder>(IMAGE_COMPARATOR) {

    // ViewHolder class
    inner class ImageViewHolder(private val binding: ItemImageBinding) : // Sử dụng ItemImageBinding
        RecyclerView.ViewHolder(binding.root) {

        fun bind(processedImage: ProcessedImage?) {
            if (processedImage == null) {
                // Có thể hiển thị placeholder nếu cần khi item là null (do placeholders)
                binding.imageView.setImageResource(R.drawable.ic_launcher_background) // Ví dụ placeholder
                binding.tvLabels.text = "Loading..."
                binding.tvUser.text = ""
            } else {
                // Load ảnh bằng Coil
                binding.imageView.load(processedImage.imageUrl) {
                    crossfade(true) // Hiệu ứng mờ dần
                    placeholder(R.drawable.ic_launcher_background) // Ảnh tạm khi đang load
                    error(R.drawable.ic_launcher_foreground) // Ảnh khi lỗi
                }
                // Hiển thị nhãn (nối các nhãn lại)
                val labelsText = if (processedImage.detectedLabels.isNotEmpty()) {
                    "Labels: ${processedImage.detectedLabels.joinToString()}"
                } else {
                    "Labels: N/A" // Hoặc không hiển thị gì
                }
                binding.tvLabels.text = labelsText
                binding.tvUser.text = "User: ${processedImage.user}"
            }
        }
    }

    // Tạo ViewHolder mới
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        // Inflate layout dùng ViewBinding
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    // Gắn dữ liệu vào ViewHolder
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageItem = getItem(position) // Lấy item từ PagingData
        holder.bind(imageItem)
    }

    // Companion object để định nghĩa DiffUtil callback
    companion object {
        private val IMAGE_COMPARATOR = object : DiffUtil.ItemCallback<ProcessedImage>() {
            // So sánh ID để biết item có giống nhau không
            override fun areItemsTheSame(oldItem: ProcessedImage, newItem: ProcessedImage): Boolean =
                oldItem.id == newItem.id

            // So sánh nội dung để biết item có thay đổi không (dùng data class tự động implement)
            override fun areContentsTheSame(oldItem: ProcessedImage, newItem: ProcessedImage): Boolean =
                oldItem == newItem
        }
    }
}