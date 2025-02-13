package com.example.launchtimestamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.launchtimestamp.ui.theme.LaunchTimestampTheme
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val MAX_TIMESTAMP_ENTRIES = 100
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val currentDatetime = sdf.format(Date())
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val setPrevDatetimeUnsorted =  sh.getStringSet("savedDatetime", linkedSetOf<String>())
        val setPrevDatetime = setPrevDatetimeUnsorted?.sortedDescending()
        var msg = "Current Launch/Redraw Timestamp:\n$currentDatetime"
        msg += "\n\nTimestamp format: yyyy/MM/dd HH:mm:ss"
        if (setPrevDatetime != null) {
            msg += "\n\nPrevious Timestamps (Max entries: $MAX_TIMESTAMP_ENTRIES)"
            for(item in setPrevDatetime.withIndex())
                msg += "\n${item.index+1}) ${item.value}"
        }
        var setDatetimeM = LinkedHashSet<String>()
        setDatetimeM.add(currentDatetime)
        if (setPrevDatetime != null) {
            if (setPrevDatetime.size > MAX_TIMESTAMP_ENTRIES - 1) {
                println("Truncating")
                setDatetimeM.addAll(setPrevDatetime.dropLast(setPrevDatetime.size - (MAX_TIMESTAMP_ENTRIES - 1)))
            } else {
                setDatetimeM.addAll(setPrevDatetime)
            }
            // We can ignore setPrevDateTime if it is null
            // Only entry in setDatetimeM will be currentDatetime which is OK.
        }

        val myEdit = sh.edit()
        myEdit.putStringSet("savedDatetime", setDatetimeM)
        myEdit.apply()
        setContent {
            LaunchTimestampTheme {
                Scaffold(modifier = Modifier
                    .safeDrawingPadding()
                    .fillMaxSize()) {
                    innerPadding ->
                    MainScreen(clearAllTimestamps = ::clearAllTimestamps,
                        addTimestamp = ::addTimestamp,
                        msg = msg,
                        modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
    private fun clearAllTimestamps() {
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        var setDatetimeM = LinkedHashSet<String>()
        val myEdit = sh.edit()
        myEdit.putStringSet("savedDatetime", setDatetimeM)
        myEdit.apply()
        this.recreate()
    }
    private fun addTimestamp() {
        this.recreate()
    }
}

@Composable
fun MainScreen(clearAllTimestamps: () -> Unit,
               addTimestamp: () -> Unit,
               msg: String, modifier: Modifier = Modifier) {
    val openAlertDialog = remember { mutableStateOf(false) }
    Column {
        Row {
            ClearTimestampsButton(onClick = {
                openAlertDialog.value = !openAlertDialog.value
                },
                modifier = modifier)
            AddTimestampsButton(onClick = { addTimestamp() },
                modifier = modifier)
        }
        ShowText(message = msg, modifier= modifier)
    }
    when {
        openAlertDialog.value -> {
            AlertDialogExample(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
                    clearAllTimestamps()
                },
                dialogTitle = "Confirm Clear Timestamps",
                dialogText = "Please confirm if you want to clear all timestamps.",
                icon = Icons.Default.Info
            )
        }
    }
}

@Composable
fun ShowText(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        modifier = modifier
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState())
    )
}

@Composable
fun ClearTimestampsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = { onClick() },
        modifier = modifier
            .padding(horizontal = 8.dp)
    ) {
        Text("Clear Timestamps")
    }
}

@Composable
fun AddTimestampsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = { onClick() },
        modifier = modifier
            .padding(horizontal = 8.dp)
    ) {
        Text("Add Timestamp")
    }
}
@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(
        icon = {
            Icon(icon, contentDescription = "Example Icon")
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}