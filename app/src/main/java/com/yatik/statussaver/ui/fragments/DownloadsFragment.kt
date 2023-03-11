package com.yatik.statussaver.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yatik.statussaver.adapters.StatusAdapter
import com.yatik.statussaver.databinding.FragmentDownloadsBinding
import com.yatik.statussaver.models.Status
import com.yatik.statussaver.repository.DefaultStatusRepository
import com.yatik.statussaver.ui.StatusViewModel
import com.yatik.statussaver.ui.StatusViewModelFactory
import com.yatik.statussaver.utils.Utilities
import com.yatik.statussaver.utils.Utilities.SAVED_FOLDER_FILE
import java.io.File

class DownloadsFragment : Fragment() {

    private var _binding: FragmentDownloadsBinding? = null
    private val binding get() = _binding!!

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StatusAdapter

    private val viewModel: StatusViewModel by viewModels {
        StatusViewModelFactory(DefaultStatusRepository(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.downloadsRecyclerView
        recyclerView.layoutManager = GridLayoutManager(context, Utilities.SPAN_COUNT)
        adapter = StatusAdapter()
        recyclerView.adapter = adapter
        submitList(fetchDownloadedStatusList(SAVED_FOLDER_FILE))

        binding.swipeLayout.setOnRefreshListener {
            submitList(fetchDownloadedStatusList(SAVED_FOLDER_FILE))
            binding.swipeLayout.isRefreshing = false
        }
    }

    private fun fetchDownloadedStatusList(savedFolder: File): List<Status> {
        return if (savedFolder.exists()) {
            viewModel.getDownloadedStatuses(savedFolder)
            viewModel.downloadedStatusList
        } else {
            listOf()
        }
    }

    private fun submitList(downloadedStatusList: List<Status>) {
        if (downloadedStatusList.isEmpty()) {
            binding.noSavedFilesText.visibility = View.VISIBLE
        } else {
            binding.noSavedFilesText.visibility = View.GONE
        }
        adapter.differ.submitList(downloadedStatusList)
    }

}