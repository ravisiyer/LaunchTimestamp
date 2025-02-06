package com.example.launchtimestamp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.launchtimestamp.ui.theme.LaunchTimestampTheme
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val MAX_TIMESTAMP_ENTRIES = 5
//        val RESIZE_TIMESTAMP_ENTRIES = 2
        val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
        val currentDatetime = sdf.format(Date())
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val setPrevDatetimeUnsorted =  sh.getStringSet("savedDatetime", linkedSetOf<String>())
        val setPrevDatetime = setPrevDatetimeUnsorted?.sortedDescending()
//        var msg = "\n Current Launch Timestamp:\n $currentDatetime"
        var msg = "\n Current Launch Timestamp: $currentDatetime"
        msg += "\n\n Timestamp format:(yyyy/MM/dd hh:mm:ss)"
        if (setPrevDatetime != null) {
            msg += "\n\n Previous Launch Timestamps (Max entries: $MAX_TIMESTAMP_ENTRIES)\n"
            for(item in setPrevDatetime.withIndex())
                msg += "\n ${item.index+1}) ${item.value}"
        }
        var setDatetimeM = LinkedHashSet<String>()
        setDatetimeM.add(currentDatetime)
        if (setPrevDatetime != null) {
            if (setPrevDatetime.size > MAX_TIMESTAMP_ENTRIES - 1) {
                println("Truncating")
                setDatetimeM.addAll(setPrevDatetime.dropLast(setPrevDatetime.size - (MAX_TIMESTAMP_ENTRIES - 1)))
            } else {
//                println("Not truncating")
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
                    ShowText(
                        message = msg,
                        modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ShowText(message: String, modifier: Modifier = Modifier) {
//fun ShowText(message: String) {
    Text(
        text = message,
    )
}
