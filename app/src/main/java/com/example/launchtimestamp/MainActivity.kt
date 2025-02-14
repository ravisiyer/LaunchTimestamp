package com.example.launchtimestamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    val openInfoDialog = remember { mutableStateOf(false) }
    Column {
        Row (modifier
            .padding(vertical = 8.dp)
        ) {
            ClearTimestampsButton(onClick = {
                openAlertDialog.value = !openAlertDialog.value
                },
                modifier = modifier)
            AddTimestampsButton(onClick = { addTimestamp() },
                modifier = modifier)
            InfoButton(onClick = {
                openInfoDialog.value = !openInfoDialog.value
            },
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
                dialogTitle = "Confirm Clear All",
                dialogText = "Please confirm if you want to clear all timestamps.",
                icon = Icons.Default.Info
            )
        }
        openInfoDialog.value -> {
            InfoDialog(
                onDismissRequest = {
                    openInfoDialog.value = false
                },
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

// Repetitive button functions could be refactored to single function
@Composable
fun ClearTimestampsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = { onClick() },
        modifier = modifier
            .padding(horizontal = 8.dp)
    ) {
        Text("Clear All")
    }
}

@Composable
fun AddTimestampsButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = { onClick() },
        modifier = modifier
            .padding(horizontal = 8.dp)
    ) {
        Text("Add")
    }
}

@Composable
fun InfoButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = { onClick() },
        modifier = modifier
            .padding(horizontal = 8.dp)
    ) {
        Text("Info")
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

@Composable
fun InfoDialog(onDismissRequest: () -> Unit) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(
//            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
        ),
    ) {
        Surface(
            modifier = Modifier
//                .fillMaxSize()
//                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(
                modifier = Modifier
                    .background(color = Color(0xffcbd4d1))
//                    .fillMaxSize(),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "App Info",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Column (
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ){
                    Text(
                        text =  "This is a very simple launch/redraw and one-touch-add timestamp recording app" +
                                " with no text associated with the timestamp.\n\n" +
                                "It automatically creates a timestamp when the app is launched.\n\n" +
                                "Clear All button: Clears all timestamps.\n" +
                                "Add button: Adds current date & time as a timestamp.\n" +
                                "Switching between portrait and landscape modes results in a redraw" +
                                " timestamp being added.\n\n" +
                                "App author: Ravi S. Iyer\n" +
                                "App date: 14 Feb. 2025",
//                                "App blog post:",
//                                "App blog post: (press to select)",
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
    //                        .padding(16.dp)
    //                    textAlign = TextAlign.Center,
                    )
                    val blogPostUrl =
                        "https://raviswdev.blogspot.com/2025/02/very-simple-one-touch-timestamp-on.html"
                    Row (
//                        modifier = Modifier
//                            .height(48.dp)
                    ) {
                    // Retrieve a ClipboardManager object
                    val clipboardManager = LocalClipboardManager.current
                        Text(
                            text =  "App blog post:",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(start = 16.dp)
//                                .padding(horizontal = 16.dp)
                                .height(48.dp)
                                .wrapContentHeight(align = Alignment.CenterVertically),
                            //                        .padding(16.dp)
                            //                    textAlign = TextAlign.Center,
                        )
//                    Button(
                    TextButton(
                        onClick = {
                            // Copy "Hello, clipboard" to the clipboard
                            clipboardManager.setText(AnnotatedString(blogPostUrl))
                        },
                        modifier = Modifier
//                            .padding(horizontal = 16.dp)
//                            .size(height = 10.dp)
                    ) {
                        Text("Tap to copy link")
                    }
                    }
                    SelectionContainer {
    //                    Text("App blog post: (press to select)\n" +
                        Text(blogPostUrl,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
    //                            .padding(top = 16.dp)
    //                            .padding(16.dp)
                            )
                    }
                    TextButton(onClick = { onDismissRequest() },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text("Dismiss",
                        )
                    }
                }
            }
        }
    }
}
