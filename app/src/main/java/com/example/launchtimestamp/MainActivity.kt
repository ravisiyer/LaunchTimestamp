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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.launchtimestamp.ui.theme.LaunchTimestampTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LaunchTimestampTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    innerPadding ->
                    ShowText(
                        message = "Android",
                        modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ShowText(message: String, modifier: Modifier = Modifier) {
    Text(
        text = message,
        fontSize = 40.sp
    )
}

@Preview(showBackground = true)
@Composable
fun LaunchTimestampPreview() {
    LaunchTimestampTheme {
        ShowText(message = "Android")
    }
}