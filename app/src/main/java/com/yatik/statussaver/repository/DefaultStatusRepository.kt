package com.yatik.statussaver.repository

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.yatik.statussaver.models.Status
import com.yatik.statussaver.utils.Utilities.*
import java.io.*
import java.util.*

class DefaultStatusRepository(
    context: Context
) : StatusRepository {

    private val contentResolver: ContentResolver = context.contentResolver
    private val mProjection = arrayOf(
        "document_id",
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.MIME_TYPE
    )

    override suspend fun getImageStatuses(collection: Uri?): MutableList<Status> {
        if (sdk29OrUp()) {
            val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} LIKE ?"
            val selectionArgs = arrayOf("image/jpeg")
            return getStatuses29OrUp(collection!!, selection, selectionArgs)
        }
        return getStatuses28OrBelow(".jpg")
    }

    override suspend fun getVideoStatuses(collection: Uri?): MutableList<Status> {
        if (sdk29OrUp()) {
            val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} LIKE ?"
            val selectionArgs = arrayOf("video/mp4")
            return getStatuses29OrUp(collection!!, selection, selectionArgs)
        }
        return getStatuses28OrBelow(".mp4")
    }

    override suspend fun getDownloadedStatuses(savedFolderFile: File): MutableList<Status> {
        val statusMutableList = mutableListOf<Status>()
        val allFiles = savedFolderFile.listFiles()!!
        Arrays.sort(allFiles) { o1: File, o2: File ->
            if (o1.lastModified() > o2.lastModified()) {
                return@sort -1
            } else if (o2.lastModified() > o1.lastModified()) {
                return@sort 1
            }
            0
        }
        for (file in allFiles) {
            statusMutableList.add(
                Status(
                    null, file.name,
                    Uri.fromFile(
                        File(file.absolutePath)
                    )
                )
            )
        }
        return statusMutableList
    }


    override suspend fun saveStatus(fileUri: Uri, mimeType: String): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return saveStatusAboveQ(fileUri, mimeType)
        }
        return saveStatusBelowQ(fileUri)
    }

    private fun getStatuses29OrUp(
        collection: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): MutableList<Status> {

        val statusMutableList = mutableListOf<Status>()
        contentResolver.query(
            collection,
            mProjection,
            selection,
            selectionArgs,
            "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        )?.use { cursor ->

            val idColumn = cursor.getColumnIndexOrThrow("document_id")
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(nameColumn)
                val mimeType = cursor.getString(mimeColumn)

                if (selectionArgs == null || mimeType == selectionArgs[0]) {
                    statusMutableList.add(
                        Status(
                            id,
                            displayName,
                            DocumentsContract.buildDocumentUriUsingTree(
                                wa_status_uri,
                                cursor.getString(0)
                            )
                        )
                    )
                }

            }
            statusMutableList
        } ?: mutableListOf()

        return statusMutableList
    }

    private fun getStatuses28OrBelow(fileExtension: String): MutableList<Status> {
        val statusMutableList = mutableListOf<Status>()
        val allFiles = folderBelowQ.listFiles()!!
        Arrays.sort(allFiles) { o1: File, o2: File ->
            if (o1.lastModified() > o2.lastModified()) {
                return@sort -1
            } else if (o2.lastModified() > o1.lastModified()) {
                return@sort 1
            }
            0
        }
        for (file in allFiles) {
            if (file.name.endsWith(fileExtension))
                statusMutableList.add(
                    Status(
                        null,
                        file.name,
                        Uri.fromFile(File(file.absolutePath))
                    )
                )
        }
        return statusMutableList
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveStatusAboveQ(collection: Uri, mimeType: String): Boolean {
        val root = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            FOLDER_NAME
        )

        if (!root.exists()) {
            root.mkdirs()
        }

        val displayName = if (collection.toString().startsWith("content://")) {
            collection.toString().substringAfterLast(".Statuses%2F")
        } else {
            collection.toString().substringAfterLast("/")
        }
        val path = root.toString() + File.separator + displayName

        if (File(path).exists()) {
            return true
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOWNLOADS + "/" + FOLDER_NAME + "/"
            )
        }

        return try {
            contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
                ?.also { resultUri ->
                    contentResolver.openOutputStream(resultUri)?.use { outputStream ->
                        contentResolver.openInputStream(collection)?.use { inputStream ->
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                            var bytesRead: Int
                            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                                outputStream.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                } ?: throw IOException("Unable to read/write data")
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun saveStatusBelowQ(fileUri: Uri): Boolean {
        val root = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            FOLDER_NAME
        )
        if (!root.exists()) {
            root.mkdirs()
        }
        val filePath = fileUri.toString()
        val path = root.toString() + File.separator + Uri.parse(filePath).lastPathSegment

        if (File(path).exists()) {
            return true
        }
        return try {
            val absolutePath: String = getAbsolutePath(filePath)
            val file = File(absolutePath)

            FileInputStream(file).use { inputStream ->
                FileOutputStream(path).use { outputStream ->
                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                }
            }
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

}