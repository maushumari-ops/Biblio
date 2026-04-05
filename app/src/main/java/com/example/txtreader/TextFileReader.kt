package com.example.txtreader

import android.content.ContentResolver
import android.net.Uri
import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.Charset
import java.nio.charset.CodingErrorAction

object TextFileReader {
    private val candidateCharsets = listOf(
        Charsets.UTF_8,
        Charset.forName("Shift_JIS"),
        Charset.forName("EUC-JP"),
        Charset.forName("UTF-16LE"),
        Charset.forName("UTF-16BE")
    )

    fun readText(contentResolver: ContentResolver, uri: Uri): String {
        val bytes = contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: throw IllegalStateException("ファイルを開けませんでした")

        if (bytes.isEmpty()) return ""

        detectBom(bytes)?.let { charset ->
            return decodeOrThrow(bytes, charset).removePrefix("\uFEFF")
        }

        candidateCharsets.forEach { charset ->
            try {
                return decodeOrThrow(bytes, charset).removePrefix("\uFEFF")
            } catch (_: CharacterCodingException) {
                // Try next charset.
            }
        }

        return bytes.toString(Charsets.UTF_8).removePrefix("\uFEFF")
    }

    private fun detectBom(bytes: ByteArray): Charset? {
        return when {
            bytes.size >= 3 && bytes[0] == 0xEF.toByte() && bytes[1] == 0xBB.toByte() && bytes[2] == 0xBF.toByte() -> Charsets.UTF_8
            bytes.size >= 2 && bytes[0] == 0xFF.toByte() && bytes[1] == 0xFE.toByte() -> Charset.forName("UTF-16LE")
            bytes.size >= 2 && bytes[0] == 0xFE.toByte() && bytes[1] == 0xFF.toByte() -> Charset.forName("UTF-16BE")
            else -> null
        }
    }

    @Throws(CharacterCodingException::class)
    private fun decodeOrThrow(bytes: ByteArray, charset: Charset): String {
        val decoder = charset.newDecoder()
        decoder.onMalformedInput(CodingErrorAction.REPORT)
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT)
        return decoder.decode(ByteBuffer.wrap(bytes)).toString()
    }
}
