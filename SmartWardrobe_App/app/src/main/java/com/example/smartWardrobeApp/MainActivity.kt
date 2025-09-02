@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.example.smartWardrobeApp

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartWardrobeApp.model.Person
import com.example.smartWardrobeApp.ui.theme.DetailScreen
import com.example.smartWardrobeApp.ui.theme.MainScreen
import com.example.smartWardrobeApp.ui.theme.Test1Theme
import com.example.smartWardrobeApp.util.copyImageToInternalStorage
import com.example.smartWardrobeApp.util.loadFromPrefs
import com.example.smartWardrobeApp.util.saveToPrefs
import com.example.smartWardrobeApp.util.generateNextName
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Test1Theme {
                val context = LocalContext.current
                val navController = rememberNavController()

                val sharedPref: SharedPreferences =
                    remember { context.getSharedPreferences("people_prefs", MODE_PRIVATE) }
                val gson = remember { Gson() }

                val peopleList = remember {
                    mutableStateListOf<Person>().apply {
                        addAll(loadFromPrefs(sharedPref, gson, key = "main"))
                    }
                }
                val customList = remember {
                    mutableStateListOf<Person>().apply {
                        addAll(loadFromPrefs(sharedPref, gson, key = "custom"))
                    }
                }

                val imagePickerLauncher =
                    rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                        if (uri != null) {
                            val path = copyImageToInternalStorage(
                                context = context,
                                uri = uri,
                                fileName = "custom_${System.currentTimeMillis()}.png"
                            )
                            if (path != null) {
                                val newName = generateNextName(customList)
                                customList.add(
                                    Person(
                                        name = newName,
                                        topOrBottom = "shirt",
                                        color = "",
                                        length = "short",
                                        index = -1,
                                        customImagePath = path
                                    )
                                )
                                saveToPrefs(sharedPref, gson, customList, key = "custom")
                            }
                        }
                    }

                NavHost(navController = navController, startDestination = "main") {
                    composable("main") {
                        MainScreen(
                            people = peopleList,
                            customList = customList,
                            onSavePeople = { newList ->
                                peopleList.clear()
                                peopleList.addAll(newList)
                                saveToPrefs(sharedPref, gson, peopleList, key = "main")
                            },
                            onSaveCustom = { newList ->
                                customList.clear()
                                customList.addAll(newList)
                                saveToPrefs(sharedPref, gson, customList, key = "custom")
                            },
                            onPickImage = { imagePickerLauncher.launch("image/*") },
                            onNavigateToDetail = { person ->
                                val encoded = URLEncoder.encode(
                                    person.name,
                                    StandardCharsets.UTF_8.toString()
                                )
                                navController.navigate("detail/$encoded")
                            },
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("detail/{encodedName}") { backStackEntry ->
                        val encodedName =
                            backStackEntry.arguments?.getString("encodedName") ?: return@composable
                        val decodedName =
                            URLDecoder.decode(encodedName, StandardCharsets.UTF_8.toString())
                        val person = peopleList.find { it.name == decodedName } ?: return@composable

                        DetailScreen(
                            person = person,
                            onDelete = {
                                peopleList.remove(person)
                                saveToPrefs(sharedPref, gson, peopleList, key = "main")
                                navController.popBackStack()
                            },
                            onBack = { navController.popBackStack() },
                            onUpdate = { updated ->
                                val idx = peopleList.indexOfFirst { it.name == person.name }
                                if (idx != -1) {
                                    peopleList[idx] = updated
                                    saveToPrefs(sharedPref, gson, peopleList, key = "main")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
