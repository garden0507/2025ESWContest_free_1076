@file:OptIn(ExperimentalMaterial3Api::class)
package com.example.smartWardrobeApp.ui.theme
import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.smartWardrobeApp.model.Person
import com.example.smartWardrobeApp.util.getImageResourceName
import java.io.File

@SuppressLint("DiscouragedApi")
@Composable
fun DetailScreen(
    person: Person,
    onDelete: () -> Unit,
    onBack: () -> Unit,
    onUpdate: (Person) -> Unit
) {
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }

    val painter = if (person.customImagePath != null) {
        rememberAsyncImagePainter(File(person.customImagePath))
    } else {
        val resId = context.resources.getIdentifier(
            getImageResourceName(person), "drawable", context.packageName
        )
        painterResource(id = if (resId != 0) resId else android.R.drawable.ic_menu_report_image)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("상세 정보") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("이름: ${person.name}", style = MaterialTheme.typography.titleLarge)
            Text("Index: ${person.index}")
            Spacer(Modifier.height(8.dp))
            Text("상의/하의: ${person.topOrBottom}")
            Text("색상: ${person.color}")
            Text("소매/바지 길이: ${person.length}")
            Spacer(Modifier.height(24.dp))
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .border(1.dp, Color.Gray)
            )
            Spacer(Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onDelete, modifier = Modifier.weight(1f)) { Text("삭제") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { showEditDialog = true }, modifier = Modifier.weight(1f)) {
                    Text("수정")
                }
            }
        }

        if (showEditDialog) {
            EditPersonDialog(
                original = person,
                onConfirm = { updated ->
                    onUpdate(updated)
                    showEditDialog = false
                },
                onDismiss = { showEditDialog = false }
            )
        }
    }
}

@Composable
fun EditPersonDialog(
    original: Person,
    onConfirm: (Person) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf(original.name) }
    var topOrBottom by remember { mutableStateOf(original.topOrBottom) }
    var color by remember { mutableStateOf(original.color) }
    var length by remember { mutableStateOf(original.length) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("정보 수정") },
        text = {
            Column {
                Text("이름")
                TextField(value = name, onValueChange = { name = it })
                Spacer(Modifier.height(8.dp))

                Text("상의/하의")
                Row {
                    RadioButton(selected = topOrBottom == "shirt", onClick = { topOrBottom = "shirt" })
                    Text("상의")
                    Spacer(Modifier.width(16.dp))
                    RadioButton(selected = topOrBottom == "pants", onClick = { topOrBottom = "pants" })
                    Text("하의")
                }

                Spacer(Modifier.height(8.dp))
                Text("색상")
                TextField(value = color, onValueChange = { color = it })

                Spacer(Modifier.height(8.dp))
                Text("소매/바지 길이")
                Row {
                    RadioButton(selected = length == "short", onClick = { length = "short" })
                    Text("반팔/반바지")
                    Spacer(Modifier.width(16.dp))
                    RadioButton(selected = length == "long", onClick = { length = "long" })
                    Text("긴팔/긴바지")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirm(
                    original.copy(
                        name = name,
                        topOrBottom = topOrBottom,
                        color = color,
                        length = length
                    )
                )
            }) { Text("저장") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } }
    )
}
