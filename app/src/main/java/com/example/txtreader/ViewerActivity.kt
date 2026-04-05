package com.example.txtreader

import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class ViewerActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_FILE_URI = "extra_file_uri"
        const val EXTRA_FILE_NAME = "extra_file_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_viewer)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        val txtContent: TextView = findViewById(R.id.txtContent)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val uriString = intent.getStringExtra(EXTRA_FILE_URI)
        val fileName = intent.getStringExtra(EXTRA_FILE_NAME).orEmpty()
        supportActionBar?.title = fileName.ifBlank { "TXT Reader" }

        if (uriString.isNullOrBlank()) {
            Toast.makeText(this, "ファイル情報がありません", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        try {
            val text = TextFileReader.readText(contentResolver, Uri.parse(uriString))
            txtContent.text = text
        } catch (e: Exception) {
            txtContent.text = ""
            Toast.makeText(this, "ファイルを読めませんでした", Toast.LENGTH_SHORT).show()
        }
    }
}
