package com.example.launchtimestamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
        var msg = "\n\nCurrent Launch Timestamp:\n$currentDatetime"
        msg += "\n\nTimestamp format: yyyy/MM/dd HH:mm:ss"
        if (setPrevDatetime != null) {
            msg += "\n\nPrevious Launch Timestamps (Max entries: $MAX_TIMESTAMP_ENTRIES)\n"
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
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    innerPadding ->
                    FilledButtonExample(onClick = {
                        clearAllTimestamps()
                        this.recreate()
                        },
                        modifier = Modifier.padding(innerPadding))
                    ShowText(
                        message = msg,
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
    }
}

@Composable
fun ShowText(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        modifier = modifier.padding(8.dp)
    )
}

@Composable
fun FilledButtonExample(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick = { onClick() },
        modifier = modifier.padding(8.dp)
    ) {
        Text("Clear Launch Timestamps")
    }
}