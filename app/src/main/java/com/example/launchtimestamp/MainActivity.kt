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
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())
        val sh = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        val setPrevDatetime =  sh.getStringSet("savedDatetime", linkedSetOf<String>())
        var msg = "\n Current Launch:   $currentDate"
        if (setPrevDatetime != null) {
            msg += "\n Previous Launches:"
            for(item in setPrevDatetime)
                msg += "\n $item"
        }
        val setDatetimeM = setPrevDatetime?.let { LinkedHashSet<String>(it) }
        setDatetimeM?.add(currentDate)
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
