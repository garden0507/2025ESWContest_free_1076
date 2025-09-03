package com.example.smartWardrobeApp.ui.theme

/*import androidx.compose.foundation.border
import androidx.compose.foundation.clickable*/
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
/*import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp*/
import com.example.smartWardrobeApp.model.Person
import com.example.smartWardrobeApp.util.remapIndex
import com.example.smartWardrobeApp.util.sendIndexAndCountToArduino

/*@Composable
fun BannerPlaceholder(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .border(1.dp, Color.Gray)
            )
            Spacer(Modifier.width(12.dp))
            Text("여기에 광고/추천 배너가 표시됩니다.")
        }
    }
}
*/

@Composable
fun ConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text("옷을 찾으셨습니까?") },
        text = { Text("옷을 꺼내셨다면 확인을 눌러주세요.") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("확인") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소") }
        }
    )
}



@Composable
fun FindButtonWithDialog(
    person: Person,
    ipAddress: String,
    allPeople: androidx.compose.runtime.snapshots.SnapshotStateList<Person>,
    onSavePeople: (List<Person>) -> Unit,
    onShowRecommendation: (Person, Int) -> Unit
) {
    var showConfirmDialog by remember(person.name) { mutableStateOf(false) }
    var sent by remember(person.name) { mutableStateOf(false) }

    var pendingName by remember { mutableStateOf<String?>(null) }
    var pendingSelected by remember { mutableStateOf<Int?>(null) }

    Column {
        Button(onClick = {
            if (!sent && allPeople.isNotEmpty()) {
                val selected = person.index
                sendIndexAndCountToArduino(
                    ip = ipAddress,
                    index = selected,
                    count = allPeople.size,
                    topOrBottom = person.topOrBottom,
                    color = person.color,
                    length = person.length
                ) {

                    val total = 8
                    for (i in allPeople.indices) {
                        val p = allPeople[i]
                        allPeople[i] = p.copy(index = remapIndex(p.index, selected, total))
                    }
                    onSavePeople(allPeople.toList())


                    pendingName = person.name
                    pendingSelected = selected

                    showConfirmDialog = true
                    sent = true
                }
            }
        }) {
            Text("찾기")
        }

        if (showConfirmDialog) {
            ConfirmDialog(
                onConfirm = {
                    pendingName?.let { target ->
                        allPeople.removeAll { it.name == target }
                        onSavePeople(allPeople.toList())
                    }
                    onShowRecommendation(
                        person.copy(name = pendingName ?: person.name),
                        pendingSelected ?: person.index
                    )

                    showConfirmDialog = false
                    sent = false
                    pendingName = null
                    pendingSelected = null
                },
                onDismiss = {
                    showConfirmDialog = false
                    sent = false
                    pendingName = null
                    pendingSelected = null
                }
            )
        }
    }
}
