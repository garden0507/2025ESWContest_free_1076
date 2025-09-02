package com.example.smartWardrobeApp.util

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.example.smartWardrobeApp.model.Person
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import androidx.core.content.edit


fun copyImageToInternalStorage(context: Context, uri: Uri, fileName: String): String? {
    return try {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val dir = File(context.filesDir, "clothes_images")
        if (!dir.exists()) dir.mkdir()
        val file = File(dir, fileName)
        val outputStream = FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        outputStream.close()
        inputStream?.close()
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun saveToPrefs(pref: SharedPreferences, gson: Gson, people: List<Person>, key: String) {
    val json = gson.toJson(people)
    pref.edit { putString(key, json) }
}


fun loadFromPrefs(pref: SharedPreferences, gson: Gson, key: String): MutableList<Person> {
    val json = pref.getString(key, null)
    val type = object : TypeToken<List<Person>>() {}.type
    return try {
        gson.fromJson<List<Person>>(json, type)?.toMutableList() ?: mutableListOf()
    } catch (e: Exception) {
        mutableListOf()
    }
}


fun getSavedIp(context: Context): String {
    return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        .getString("arduino_ip", "") ?: ""
}

fun saveIp(context: Context, ip: String) {
    context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        .edit { putString("arduino_ip", ip) }
}


