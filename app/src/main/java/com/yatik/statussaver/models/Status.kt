package com.yatik.statussaver.models

import android.net.Uri

data class Status (
    val id: Long?,
    val fileName: String,
    val contentUri: Uri
)