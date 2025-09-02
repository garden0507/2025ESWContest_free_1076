package com.example.smartWardrobeApp.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.smartWardrobeApp.model.Person
import com.example.smartWardrobeApp.util.deriveColorNameFromImage
import com.example.smartWardrobeApp.util.generateNextIndex
import com.example.smartWardrobeApp.util.generateNextName
import com.example.smartWardrobeApp.util.getImageResourceName
import com.example.smartWardrobeApp.util.getSavedIp
import com.example.smartWardrobeApp.util.rebase
import com.example.smartWardrobeApp.util.saveIp
import com.example.smartWardrobeApp.util.sendIndexAndCountToArduino
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@SuppressLint("DiscouragedApi")
@Composable
fun MainScreen(
    people: SnapshotStateList<Person>,
    customList: SnapshotStateList<Person>,
    onSavePeople: (List<Person>) -> Unit,
    onSaveCustom: (List<Person>) -> Unit,
    onPickImage: () -> Unit,
    onNavigateToDetail: (Person) -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var ipAddress by remember { mutableStateOf(getSavedIp(context)) }
    var showDialog by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(0) }
    var newName by remember { mutableStateOf("") }
    var topOrBottom by remember { mutableStateOf("shirt") }
    var color by remember { mutableStateOf("") }
    var length by remember { mutableStateOf("short") }
    var selectedCustom: Person? by remember { mutableStateOf(null) }
    var showCustomPicker by remember { mutableStateOf(false) }
    var newIndex by remember { mutableStateOf(-1) }
    var recommendPerson: Person? by remember { mutableStateOf(null) }

    Scaffold(
        //bottomBar = { BannerPlaceholder { /* TODO: 배너 클릭 처리 */ } }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            TextField(
                value = ipAddress,
                onValueChange = {
                    ipAddress = it
                    saveIp(context, it)
                },
                label = { Text("와이파이 IP 주소") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(onClick = onPickImage, modifier = Modifier.fillMaxWidth()) {
                Text("옷 커스텀하기")
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (people.size < 8) {
                        val index = generateNextIndex(people)
                        if (index != -1) {
                            newIndex = index
                            sendIndexAndCountToArduino(ipAddress, newIndex, people.size)
                            newName = generateNextName(people)
                            currentStep = 0
                            selectedCustom = null
                            showDialog = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("옷 추가하려면 클릭") }

            Spacer(Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.weight(1f, fill = true)) {
                items(
                    items = people,
                    key = { it.name }
                ) { person ->
                    val painter = if (person.customImagePath != null) {
                        rememberAsyncImagePainter(File(person.customImagePath))
                    } else {
                        val resId = context.resources.getIdentifier(
                            getImageResourceName(person), "drawable", context.packageName
                        )
                        painterResource(id = if (resId != 0) resId else android.R.drawable.ic_menu_report_image)
                    }
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { onNavigateToDetail(person) }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(painter = painter, contentDescription = null, modifier = Modifier.size(60.dp))
                            Spacer(Modifier.width(12.dp))
                            Column(verticalArrangement = Arrangement.Center) {
                                Text(text = person.name, style = MaterialTheme.typography.bodyLarge)
                                Spacer(Modifier.height(4.dp))
                                Text(text = "Index: ${person.index}", style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(Modifier.weight(1f))

                            FindButtonWithDialog(
                                person = person,
                                ipAddress = ipAddress,
                                allPeople = people,
                                onSavePeople = onSavePeople,
                                onShowRecommendation = { p: Person, _: Int ->
                                    recommendPerson = p
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("옷 정보 입력") },
            text = {
                Column {
                    when (currentStep) {
                        0 -> {
                            Text("이름 입력")
                            TextField(value = newName, onValueChange = { newName = it })
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { showCustomPicker = true }) { Text("커스텀 옷 선택하기") }
                        }
                        1 -> {
                            Text("상의/하의 선택")
                            Row {
                                RadioButton(selected = topOrBottom == "shirt", onClick = { topOrBottom = "shirt" })
                                Text("상의")
                                Spacer(Modifier.width(16.dp))
                                RadioButton(selected = topOrBottom == "pants", onClick = { topOrBottom = "pants" })
                                Text("하의")
                            }
                        }
                        2 -> {
                            Text("색상 입력")
                            TextField(value = color, onValueChange = { color = it })
                        }
                        3 -> {
                            Text("소매/바지 길이")
                            Row {
                                RadioButton(selected = length == "short", onClick = { length = "short" })
                                Text("반팔/반바지")
                                Spacer(Modifier.width(16.dp))
                                RadioButton(selected = length == "long", onClick = { length = "long" })
                                Text("긴팔/긴바지")
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    when (currentStep) {
                        in 0..2 -> currentStep++
                        3 -> {
                            if (newIndex != -1) {
                                people.add(
                                    Person(
                                        name = newName,
                                        topOrBottom = topOrBottom,
                                        color = color,
                                        length = length,
                                        index = newIndex,
                                        customImagePath = selectedCustom?.customImagePath
                                    )
                                )
                                rebase(people, selected = newIndex)
                                onSavePeople(people.toList())
                                newIndex = -1
                            }
                            showDialog = false
                        }
                    }
                }) { Text(if (currentStep < 3) "다음" else "완료") }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (newIndex != -1) {
                        rebase(people, selected = newIndex)
                        onSavePeople(people.toList())
                        newIndex = -1
                    }
                    showDialog = false
                }) { Text("취소") }
            }
        )
    }

    if (showCustomPicker) {
        AlertDialog(
            onDismissRequest = { showCustomPicker = false },
            title = { Text("커스텀 옷 선택") },
            text = {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    customList.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedCustom = item
                                    newName = item.name
                                    showCustomPicker = false
                                    val path = item.customImagePath
                                    if (!path.isNullOrEmpty()) {
                                        scope.launch(Dispatchers.IO) {
                                            val auto = deriveColorNameFromImage(path)
                                            if (auto != null) {
                                                withContext(Dispatchers.Main) { color = auto }
                                            }
                                        }
                                    }
                                }
                                .padding(8.dp)
                        ) {
                            val painter = rememberAsyncImagePainter(File(item.customImagePath ?: ""))
                            Image(painter = painter, contentDescription = null, modifier = Modifier.size(48.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(item.name)
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = {
                                customList.remove(item)
                                onSaveCustom(customList.toList())
                            }) { Icon(Icons.Default.Delete, contentDescription = "삭제") }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showCustomPicker = false }) { Text("닫기") } },
            dismissButton = {}
        )
    }

    recommendPerson?.let { base ->
        RecommendationDialog(
            person = base,
            people = people,
            onDismiss = { recommendPerson = null },
            onSavePeople = onSavePeople
        )
    }
}
