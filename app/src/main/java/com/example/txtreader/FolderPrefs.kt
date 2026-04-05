package com.example.txtreader

import android.content.Context

object FolderPrefs {
    private const val PREFS_NAME = "txt_reader_prefs"
    private const val KEY_FOLDER_URI = "folder_uri"

    fun saveFolderUri(context: Context, uriString: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_FOLDER_URI, uriString)
            .apply()
    }

    fun getFolderUri(context: Context): String? {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_FOLDER_URI, null)
    }
}
