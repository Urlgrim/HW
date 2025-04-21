package com.example.imagedetection.data

data class ProcessedImage(
    val id: Long, // Giữ lại ID để DiffUtil hoạt động tốt
    val imageUrl: String, // URL để hiển thị ảnh
    val tags: String,     // Có thể giữ lại tags gốc từ Pixabay
    val user: String,     // Có thể giữ lại user gốc
    val detectedLabels: List<String> // Danh sách nhãn từ ML Kit
)