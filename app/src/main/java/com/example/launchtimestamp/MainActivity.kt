package com.example.launchtimestamp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
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
        msg += "\nFrom within app, to add new (redraw) timestamp, switch between portrait and landscape modes."
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
}

@Composable
fun MainScreen(clearAllTimestamps: () -> Unit, msg: String, modifier: Modifier = Modifier) {
    val openAlertDialog = remember { mutableStateOf(false) }
//    Log.d("My App","MainScreen")
    // Code works only if button and text are defined in this function itself.
    // If I call FilledButtonExample() and ShowText(), the button click does not work!
    // I cannot figure out why given my very, very limited knowledge of Android prog.
    // So have commented out FilledButtonExample() and ShowText() code.
    // Update: The verticalScroll modifier to Text creates the problem!
    // Column layout seems to be the fix.
    Column {
        Button(onClick = {
//            Log.d("My App","Button onClick invoked")
            openAlertDialog.value = !openAlertDialog.value
        },
            modifier = modifier
                .padding(horizontal = 8.dp)
        ) {
            Text("Clear Timestamps")
        }
        Text(
            text = msg,
            modifier = modifier
                .padding(horizontal = 8.dp)
                .verticalScroll(rememberScrollState())
        )
    }
    when {
        openAlertDialog.value -> {
            AlertDialogExample(
                onDismissRequest = { openAlertDialog.value = false },
                onConfirmation = {
                    openAlertDialog.value = false
//                    Log.d("My App","Confirmation registered") // Add logic here to handle confirmation.
                    clearAllTimestamps()
                },
                dialogTitle = "Confirm Clear Timestamps",
                dialogText = "Please confirm if you want to clear all timestamps.",
                icon = Icons.Default.Info
            )
        }
    }
}

//@Composable
//fun ShowText(message: String, modifier: Modifier = Modifier) {
//    Text(
//        text = message,
//        modifier = modifier
////            .safeDrawingPadding()
//            .padding(horizontal = 8.dp)
////            .padding(8.dp)
//            .verticalScroll(rememberScrollState())
//    )
//}

//@Composable
//fun FilledButtonExample(onClick:  () -> Unit, modifier: Modifier = Modifier) {
////    Log.d("My App","FilledButtonExample start")
////    val openAlertDialog = remember { mutableStateOf(false) }
////    Button(
//    Button(onClick = {
//        onClick();
////        Log.d("My App","Button onClick invoked")
////        openAlertDialog.value = !openAlertDialog.value
//                     },
//        modifier = modifier
////            .safeDrawingPadding()
//            .padding(horizontal = 8.dp)
////            .padding(bottom = 8.dp)
////            .padding(8.dp)
//            .clickable(enabled = true,
//                onClick = {onClick();})
//    ) {
//        Text("Clear Timestamps")
//    }
////    when {
////        // ...
////        openAlertDialog.value -> {
////            AlertDialogExample(
////                onDismissRequest = { openAlertDialog.value = false },
////                onConfirmation = {
////                    openAlertDialog.value = false
////                    println("Confirmation registered") // Add logic here to handle confirmation.
////                    onClick();
////                },
////                dialogTitle = "Alert dialog example",
////                dialogText = "This is an example of an alert dialog with buttons.",
////                icon = Icons.Default.Info
////            )
////        }
////    }
//}

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