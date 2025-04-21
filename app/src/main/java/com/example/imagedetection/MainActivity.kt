// MainActivity.kt
package com.example.imagedetection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.viewModels // Import extension function
import androidx.core.view.isVisible // Extension để set visibility
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.example.imagedetection.adapter.ImageAdapter
import com.example.imagedetection.databinding.ActivityMainBinding // Import ViewBinding class
import com.example.imagedetection.viewmodel.GalleryViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: GalleryViewModel by viewModels() // Khởi tạo ViewModel bằng extension
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater) // Inflate layout bằng ViewBinding
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        observeViewModel()
        handleLoadingStates()
    }

    private fun setupRecyclerView() {
        imageAdapter = ImageAdapter() // Khởi tạo adapter
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2) // Hiển thị dạng lưới 2 cột
            adapter = imageAdapter
            // Optional: Thêm ItemDecoration nếu muốn có khoảng cách giữa các item
        }
    }

    private fun setupSearch() {
        binding.etSearch.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true // Đã xử lý sự kiện
            } else {
                false // Chưa xử lý
            }
        }
        binding.btnSearch.setOnClickListener {
            performSearch()
        }
    }

    private fun performSearch() {
        val query = binding.etSearch.text.toString()
        viewModel.searchImages(query)
        // Có thể ẩn bàn phím ở đây nếu muốn
        binding.recyclerView.scrollToPosition(0) // Cuộn lên đầu khi tìm kiếm mới
    }


    private fun observeViewModel() {
        // Quan sát Flow PagingData từ ViewModel
        lifecycleScope.launch { // Chạy coroutine trong lifecycle của Activity
            viewModel.imageFlow.collectLatest { pagingData -> // collectLatest để hủy bỏ lần thu thập trước nếu có dữ liệu mới
                imageAdapter.submitData(pagingData) // Gửi dữ liệu mới cho Adapter
            }
        }
    }

    private fun handleLoadingStates() {
        // Quan sát trạng thái load của Adapter
        lifecycleScope.launch {
            imageAdapter.loadStateFlow.collectLatest { loadStates ->
                // Hiển thị ProgressBar khi đang load (refresh hoặc append)
                binding.progressBar.isVisible = loadStates.refresh is LoadState.Loading ||
                        loadStates.append is LoadState.Loading

                // Xử lý trạng thái lỗi khi refresh
                val refreshState = loadStates.refresh
                if (refreshState is LoadState.Error) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error loading images: ${refreshState.error.localizedMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                    // Có thể hiển thị một thông báo lỗi trên màn hình thay vì Toast
                }

                // Xử lý trạng thái lỗi khi append (load thêm trang)
                val appendState = loadStates.append
                if (appendState is LoadState.Error) {
                    Toast.makeText(
                        this@MainActivity,
                        "Error loading more images: ${appendState.error.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}