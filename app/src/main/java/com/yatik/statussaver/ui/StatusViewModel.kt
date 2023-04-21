package com.yatik.statussaver.ui

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yatik.statussaver.models.Status
import com.yatik.statussaver.repository.StatusRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class StatusViewModel(
    private val repository: StatusRepository
) : ViewModel(){

    private var _imageStatuses = mutableListOf<Status>()
    val imageStatusList
        get() = _imageStatuses

    private var _videoStatuses = mutableListOf<Status>()
    val videoStatusList
        get() = _videoStatuses

    private var _downloadedStatuses = mutableListOf<Status>()
    val downloadedStatusList
        get() = _downloadedStatuses

    private var _fileSaved = MutableLiveData<Boolean>()
    val isFileSaved: LiveData<Boolean> get() = _fileSaved

    fun getImageStatuses(folderUri: Uri) = viewModelScope.launch {
        _imageStatuses = repository.getImageStatuses(folderUri)
    }

    fun getVideoStatuses(folderUri: Uri) = viewModelScope.launch {
        _videoStatuses = repository.getVideoStatuses(folderUri)
    }

    fun getDownloadedStatuses(savedFolderFile: File) = viewModelScope.launch {
        _downloadedStatuses = repository.getDownloadedStatuses(savedFolderFile)
    }

    fun saveStatus(fileUri: Uri, mimeType: String) = viewModelScope.launch(Dispatchers.IO) {

        val result = withContext(Dispatchers.IO) {
            repository.saveStatus(fileUri, mimeType)
        }
        withContext(Dispatchers.Main) {
            _fileSaved.value = result
        }
    }

}

class StatusViewModelFactory(private val repository: StatusRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatusViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatusViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}