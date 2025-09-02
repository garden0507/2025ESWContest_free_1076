package com.example.smartWardrobeApp.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.OutputStream
import java.net.Socket

private fun sanitize(s: String?): String =
    (s ?: "").replace(",", " ").replace("\n", " ").trim()

fun sendIndexAndCountToArduino(
    ip: String,
    index: Int,
    count: Int,
    topOrBottom: String? = null,
    color: String? = null,
    length: String? = null,
    onReturn: (() -> Unit)? = null
) {
    if (count <= 0) { return }

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val port = 8080
            Socket(ip, port).use { socket ->
                val outputStream: OutputStream = socket.getOutputStream()

                val hasExtra =
                    !topOrBottom.isNullOrBlank() ||
                            !color.isNullOrBlank() ||
                            !length.isNullOrBlank()

                val message = if (hasExtra) {
                    "${index},${count}," +
                            "${sanitize(topOrBottom)}," +
                            "${sanitize(color)}," +
                            "${sanitize(length)}\n"
                } else {
                    "${index},${count}\n"
                }

                outputStream.write(message.toByteArray())
                outputStream.flush()
            }
            onReturn?.let {
                CoroutineScope(Dispatchers.Main).launch { it() }
            }
        } catch (e: Exception) {
            Log.e("ArduinoComm", "소켓 통신 오류: ${e.message}")
        }
    }
}
