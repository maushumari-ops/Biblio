package com.example.txtreader

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var btnSelectFolder: MaterialButton
    private lateinit var txtFolderPath: TextView
    private lateinit var edtSearch: EditText
    private lateinit var recyclerView: RecyclerView
    private lateinit var txtEmpty: TextView

    private val adapter = FileListAdapter { fileItem ->
        val intent = Intent(this, ViewerActivity::class.java).apply {
            putExtra(ViewerActivity.EXTRA_FILE_URI, fileItem.uriString)
            putExtra(ViewerActivity.EXTRA_FILE_NAME, fileItem.name)
        }
        startActivity(intent)
    }

    private var allFiles: List<FileItem> = emptyList()

    private val folderPicker = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) {
            handleSelectedFolder(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        bindViews()
        setupList()
        setupActions()

        val savedFolder = FolderPrefs.getFolderUri(this)
        if (savedFolder.isNullOrBlank()) {
            showEmpty("フォルダを選択してください")
        } else {
            loadFolder(Uri.parse(savedFolder))
        }
    }

    private fun bindViews() {
        btnSelectFolder = findViewById(R.id.btnSelectFolder)
        txtFolderPath = findViewById(R.id.txtFolderPath)
        edtSearch = findViewById(R.id.edtSearch)
        recyclerView = findViewById(R.id.recyclerFiles)
        txtEmpty = findViewById(R.id.txtEmpty)
    }

    private fun setupList() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun setupActions() {
        btnSelectFolder.setOnClickListener {
            folderPicker.launch(null)
        }

        edtSearch.addTextChangedListener(SimpleTextWatcher { text ->
            filterFiles(text)
        })
    }

    private fun handleSelectedFolder(uri: Uri) {
        try {
            contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        } catch (_: SecurityException) {
            // Some providers may not support write permission. Try read-only.
            try {
                contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: SecurityException) {
                // Ignore and attempt to use the URI anyway.
            }
        }

        FolderPrefs.saveFolderUri(this, uri.toString())
        loadFolder(uri)
    }

    private fun loadFolder(uri: Uri) {
        val tree = DocumentFile.fromTreeUri(this, uri)
        if (tree == null || !tree.isDirectory) {
            allFiles = emptyList()
            txtFolderPath.text = "フォルダを開けませんでした"
            showEmpty("フォルダを開けませんでした")
            return
        }

        txtFolderPath.text = getDisplayFolderPath(uri)

        allFiles = tree.listFiles()
            .filter { it.isFile }
            .filter { it.name?.let(::isSupportedFileName) == true }
            .sortedBy { it.name?.lowercase() ?: "" }
            .map {
                FileItem(
                    name = it.name ?: "(no name)",
                    uriString = it.uri.toString(),
                    size = it.length(),
                    lastModified = it.lastModified()
                )
            }

        filterFiles(edtSearch.text?.toString().orEmpty())
    }

    private fun filterFiles(query: String) {
        val normalized = query.trim().lowercase()
        val filtered = if (normalized.isBlank()) {
            allFiles
        } else {
            allFiles.filter { it.name.lowercase().contains(normalized) }
        }

        adapter.submitList(filtered)
        if (filtered.isEmpty()) {
            val message = if (allFiles.isEmpty()) "対象ファイルがありません" else "一致するファイルがありません"
            showEmpty(message)
        } else {
            showList()
        }
    }

    private fun showEmpty(message: String) {
        txtEmpty.text = message
        txtEmpty.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
    }

    private fun showList() {
        txtEmpty.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
    }

    private fun isSupportedFileName(name: String): Boolean {
        val lower = name.lowercase()
        return lower.endsWith(".txt") || lower.endsWith(".log") || lower.endsWith(".csv") || lower.endsWith(".md")
    }

    private fun getDisplayFolderPath(uri: Uri): String {
        return try {
            val treeDocId = DocumentsContract.getTreeDocumentId(uri)
            "選択中: $treeDocId"
        } catch (_: Exception) {
            uri.toString()
        }
    }
}
