package com.example.launchtimestamp

//import android.content.Context
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.sp
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
        val MAX_TIMESTAMP_ENTRIES = 3
        val RESIZE_TIMESTAMP_ENTRIES = 2
//        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val sdf = SimpleDateFormat("yyyy/MM/dd hh:mm:ss")
        val currentDate = sdf.format(Date())
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val setPrevDatetimeUnsorted =  sh.getStringSet("savedDatetime", linkedSetOf<String>())
        val setPrevDatetime = setPrevDatetimeUnsorted?.sortedDescending()
        var msg = "\n Current Launch (yyyy/MM/dd hh:mm:ss):\n $currentDate"
        if (setPrevDatetime != null) {
            msg += "\n Previous Launches:"
            for(item in setPrevDatetime)
                msg += "\n $item"
        }

        var setDatetimeM = LinkedHashSet<String>()
        setDatetimeM.add(currentDate)
        if (setPrevDatetime != null) {
            if (setPrevDatetime.size > MAX_TIMESTAMP_ENTRIES ) {
                // Truncate set (inefficient as for loop does not break out; will do that next
                // after exploring drop)
                println("Truncating")
                for(indexedValue in setPrevDatetime.withIndex())
                    if (indexedValue.index < RESIZE_TIMESTAMP_ENTRIES) {
                        setDatetimeM.add(indexedValue.value)
                    }
            } else {
                println("Not truncating")
//                setDatetimeM = LinkedHashSet(setPrevDatetime)
                setDatetimeM.addAll(setPrevDatetime)
            }
            // We can ignore setPrevDateTime if it is null
            // Only entry in setDatetimeM will be currentDate which is OK.
        }
//        val setDatetimeM = setPrevDatetime?.let { LinkedHashSet<String>(it) }

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
