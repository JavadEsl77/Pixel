package com.javadEsl.pixel.ui.myDownload

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.javadEsl.pixel.R
import com.javadEsl.pixel.databinding.FragmentMyDownloadBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class MyDownloadFragment : Fragment(R.layout.fragment_my_download),
    MyDownloadAdapter.OnItemClickListener {
    private val viewModel by viewModels<MyDownloadViewModel>()
    private var _binding: FragmentMyDownloadBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMyDownloadBinding.bind(view)
        val adapter = MyDownloadAdapter(viewModel.getDownloadPictures(), this)

        binding.apply {

            if (adapter.downloadList.isEmpty()) {
                recyclerViewMyDownload.isVisible = false
            } else {
                recyclerViewMyDownload.setHasFixedSize(true)
                recyclerViewMyDownload.itemAnimator = null
                recyclerViewMyDownload.adapter = adapter

            }
        }
        Log.e("TAG", "onViewCreated:${adapter.downloadList} ")

    }

    override fun onItemClick(photo: File) {
        TODO("Not yet implemented")
    }
}