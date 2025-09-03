package com.example.smartWardrobeApp.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.smartWardrobeApp.model.Person
import com.example.smartWardrobeApp.util.getImageResourceName
import com.example.smartWardrobeApp.util.getSavedIp
import com.example.smartWardrobeApp.util.remapIndex
import com.example.smartWardrobeApp.util.sendIndexAndCountToArduino
import com.example.smartWardrobeApp.weather.WeatherClient
import com.example.smartWardrobeApp.weather.LengthFilter
import com.example.smartWardrobeApp.weather.lengthFilterFromTempHum
import java.io.File

@SuppressLint("DiscouragedApi")
@Composable
fun RecommendationDialog(
    person: Person,
    people: MutableList<Person>,
    onDismiss: () -> Unit,
    onSavePeople: (List<Person>) -> Unit
) {
    val context = LocalContext.current
    val ipAddress = getSavedIp(context)
    val oppositeType = if (person.topOrBottom == "shirt") "pants" else "shirt"


    var tempC by remember { mutableStateOf<Double?>(null) }
    var loading by remember { mutableStateOf(true) }
    var weatherError by remember { mutableStateOf<String?>(null) }
    var humidityPct by remember { mutableStateOf<Int?>(null) }


    LaunchedEffect(person) {
        loading = true
        weatherError = null

        val now = WeatherClient.fetchTempAndHumidity()
        tempC = now.tempC
        humidityPct = now.humidityPct

        if (tempC == null && humidityPct == null) {
            weatherError = "날씨를 불러오지 못했어요 (기존 방식으로 추천합니다)."
        }
        loading = false
    }

    var filtered = remember(people, person) {
        people.filter { it.topOrBottom == oppositeType && it.name != person.name }
    }

    when (lengthFilterFromTempHum(oppositeType, tempC, humidityPct)) {
        LengthFilter.SHORT_ONLY -> {
            filtered = filtered.filter { it.length == "short" }
        }
        LengthFilter.LONG_ONLY -> {
            filtered = filtered.filter { it.length == "long" }
        }
        LengthFilter.ANY -> {}
    }


    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    var selectedPerson by remember { mutableStateOf<Person?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("이런 옷은 어때요?") },
        text = {
            Column {
                if (loading) {
                    Text("날씨를 불러오는 중...")
                    Spacer(Modifier.height(8.dp))
                } else {
                    tempC?.let { Text("현재 기온: ${"%.1f".format(it)}℃") }
                    weatherError?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    Spacer(Modifier.height(8.dp))
                }

                if (filtered.isEmpty()) {
                    Text("추천할 옷이 없습니다.")
                } else {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        filtered.forEach { item ->
                            val painter = if (item.customImagePath != null) {
                                rememberAsyncImagePainter(File(item.customImagePath))
                            } else {
                                val resId = context.resources.getIdentifier(
                                    getImageResourceName(item), "drawable", context.packageName
                                )
                                painterResource(id = if (resId != 0) resId else android.R.drawable.ic_menu_report_image)
                            }

                            Row(
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Image(painter = painter, contentDescription = null, modifier = Modifier.size(48.dp))
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name)
                                    Text("Index: ${item.index}", style = MaterialTheme.typography.bodySmall)
                                }
                                Button(onClick = {
                                    sendIndexAndCountToArduino(ipAddress, item.index, people.size) {
                                        selectedIndex = item.index
                                        selectedPerson = item
                                        showConfirmDialog = true
                                    }
                                }) { Text("찾기") }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("추천 안 받을래요") } },
        dismissButton = {}
    )

    if (showConfirmDialog && selectedIndex != null && selectedPerson != null) {
        ConfirmDialog(
            onConfirm = {
                val total = 8
                val selected = selectedIndex!!
                val remaining = people.filter { it.name != selectedPerson!!.name }
                val rotated = remaining.map { p -> p.copy(index = remapIndex(p.index, selected, total)) }
                people.clear(); people.addAll(rotated)
                onSavePeople(people.toList())

                showConfirmDialog = false
                selectedIndex = null
                selectedPerson = null
                onDismiss()
            },
            onDismiss = {
                val total = 8
                val selected = selectedIndex!!
                for (i in people.indices) {
                    val p = people[i]
                    people[i] = p.copy(index = remapIndex(p.index, selected, total))
                }
                onSavePeople(people.toList())

                showConfirmDialog = false
                selectedIndex = null
                selectedPerson = null
                onDismiss()
            }
        )

    }
}
