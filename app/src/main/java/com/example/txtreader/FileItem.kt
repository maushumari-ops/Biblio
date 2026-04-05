package com.example.txtreader

data class FileItem(
    val name: String,
    val uriString: String,
    val size: Long = 0L,
    val lastModified: Long = 0L
)
