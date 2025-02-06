package com.example.launchtimestamp

//import android.content.Context
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
import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.sp
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
        val prevDatetime = sh.getString("savedDatetime", "")
        val myEdit = sh.edit()
        myEdit.putString("savedDatetime", currentDate.toString())
        myEdit.apply()
//        val msg = "\nCurrent Launch:\n $currentDate\n Prev. Launch:\n $prevDatetime\n"
        val msg = "\n Current Launch:   $currentDate\n Previous Launch: $prevDatetime\n"
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

@Preview(showBackground = true)
@Composable
fun LaunchTimestampPreview() {
    LaunchTimestampTheme {
        ShowText(message = "Test")
    }
}