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
import com.yatik.statussaver.R
import com.yatik.statussaver.adapters.StatusAdapter
import com.yatik.statussaver.databinding.FragmentImageBinding
import com.yatik.statussaver.models.Status
import com.yatik.statussaver.repository.DefaultStatusRepository
import com.yatik.statussaver.ui.StatusViewModel
import com.yatik.statussaver.ui.StatusViewModelFactory
import com.yatik.statussaver.utils.Utilities.SPAN_COUNT
import com.yatik.statussaver.utils.Utilities.folderAboveQ
import com.yatik.statussaver.utils.Utilities.folderBelowQ
import com.yatik.statussaver.utils.Utilities.sdk29OrUp
import com.yatik.statussaver.utils.Utilities.waPackageName
import com.yatik.statussaver.utils.Utilities.wa_status_uri

class ImageFragment : Fragment() {

    private var _binding: FragmentImageBinding? = null
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
        _binding = FragmentImageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(context, SPAN_COUNT)
        adapter = StatusAdapter()
        recyclerView.adapter = adapter

        val folderUri = DocumentsContract.buildChildDocumentsUriUsingTree(
            wa_status_uri,
            DocumentsContract.getDocumentId(wa_status_uri)
        )
        submitList(fetchImageStatusList(folderUri))

        binding.swipeLayout.setOnRefreshListener {
            submitList(fetchImageStatusList(folderUri))
            binding.swipeLayout.isRefreshing = false
        }
    }

    private fun fetchImageStatusList(folderUri: Uri): List<Status> {
        return if ((folderAboveQ.exists() && sdk29OrUp()) || (folderBelowQ.exists() && !sdk29OrUp())
        ) {
            viewModel.getImageStatuses(folderUri)
            viewModel.imageStatusList
        } else {
            listOf()
        }
    }

    private fun submitList(imageStatusList: List<Status>) {
        if (imageStatusList.isEmpty()) {
            noFilesView()
        } else {
            val noFilesFound = requireView().findViewById<View>(R.id.no_files_found_img)
            noFilesFound.visibility = View.GONE
        }
        adapter.differ.submitList(imageStatusList)
    }

    private fun noFilesView() {
        val noFilesFound = requireView().findViewById<View>(R.id.no_files_found_img)
        noFilesFound.visibility = View.VISIBLE
        val openWA = noFilesFound.findViewById<Button>(R.id.no_files_button)
        openWA.setOnClickListener {
            var launchIntent: Intent? = null
            try {
                launchIntent =
                    requireActivity().packageManager.getLaunchIntentForPackage(waPackageName)
            } catch (ignored: Exception) {
            }
            if (launchIntent == null) {
                startActivity(Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://play.google.com/store/apps/details?id=" + waPackageName)))
            } else {
                startActivity(launchIntent)
            }
        }
    }

}