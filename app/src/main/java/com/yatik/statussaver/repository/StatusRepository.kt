package com.yatik.statussaver.repository

import android.net.Uri
import com.yatik.statussaver.models.Status
import java.io.File

interface StatusRepository {

    suspend fun getImageStatuses(collection: Uri?): MutableList<Status>
    suspend fun getVideoStatuses(collection: Uri?): MutableList<Status>
    suspend fun getDownloadedStatuses(savedFolderFile: File): MutableList<Status>
    suspend fun saveStatus(fileUri: Uri, mimeType: String): Boolean

}