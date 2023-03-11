package com.yatik.statussaver.ui.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yatik.statussaver.adapters.StatusAdapter
import com.yatik.statussaver.R
import com.yatik.statussaver.databinding.FragmentVideoBinding
import com.yatik.statussaver.models.Status
import com.yatik.statussaver.repository.DefaultStatusRepository
import com.yatik.statussaver.ui.StatusViewModel
import com.yatik.statussaver.ui.StatusViewModelFactory
import com.yatik.statussaver.utils.Utilities
import com.yatik.statussaver.utils.Utilities.folderAboveQ
import com.yatik.statussaver.utils.Utilities.folderBelowQ

class VideoFragment : Fragment() {

    private var _binding: FragmentVideoBinding? = null
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
        _binding = FragmentVideoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.videoRecyclerView
        recyclerView.layoutManager = GridLayoutManager(context, Utilities.SPAN_COUNT)
        adapter = StatusAdapter()
        recyclerView.adapter = adapter

        val folderUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            Utilities.wa_status_uri,
            DocumentsContract.getDocumentId(Utilities.wa_status_uri)
        )
        submitList(fetchVideoStatusList(folderUri))

        binding.swipeLayout.setOnRefreshListener {
            submitList(fetchVideoStatusList(folderUri))
            binding.swipeLayout.isRefreshing = false
        }
    }

    private fun fetchVideoStatusList(folderUri: Uri): List<Status> {
        return if ((folderAboveQ.exists() && Utilities.sdk29OrUp()) || (folderBelowQ.exists() && !Utilities.sdk29OrUp())
        ) {
            viewModel.getVideoStatuses(folderUri)
            viewModel.videoStatusList
        } else {
            listOf()
        }
    }

    private fun submitList(videoStatusList: List<Status>) {
        if (videoStatusList.isEmpty()) {
            noFilesView()
        } else {
            val noFilesFound = requireView().findViewById<View>(R.id.no_files_found_img)
            noFilesFound.visibility = View.GONE
        }
        adapter.differ.submitList(videoStatusList)
    }

    private fun noFilesView() {
        val noFilesFound = requireView().findViewById<View>(R.id.no_files_found_img)
        noFilesFound.visibility = View.VISIBLE
        val openWA = noFilesFound.findViewById<Button>(R.id.no_files_button)
        openWA.setOnClickListener {
            var launchIntent: Intent? = null
            try {
                launchIntent =
                    requireActivity().packageManager.getLaunchIntentForPackage(Utilities.waPackageName)
            } catch (ignored: Exception) {
            }
            if (launchIntent == null) {
                startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=" + Utilities.waPackageName)))
            } else {
                startActivity(launchIntent)
            }
        }
    }

}