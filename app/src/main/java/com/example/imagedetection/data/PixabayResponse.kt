// data/PixabayResponse.kt
package com.example.imagedetection.data

data class PixabayResponse(
    val total: Int,
    val totalHits: Int,
    val hits: List<ImageItem>
)
