package com.laksh.mydocscannerapp.activity

import DetectFaces
import LabelImages
import ScanBarcodes
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.laksh.mydocscannerapp.composescreen.TextRecognizer
import com.laksh.mydocscannerapp.ui.theme.MyDocScannerAppTheme
import com.laksh.mydocscannerapp.ui.theme.Pink80

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val myNavController = rememberNavController()
            MyDocScannerAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = myNavController,
                        startDestination = "main"
                    ) {
                        composable("main") { GreetingPreview(myNavController) }
                        composable("text_recognizer") { TextRecognizer(/*myNavController*/) }
                        composable("detect_faces") { DetectFaces() }
                        composable("scan_barcodes") { ScanBarcodes() }
                        composable("label_images") { LabelImages() }
                    }
                }
            }
        }
    }
}


//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview(myNavController:NavController) {
    MyDocScannerAppTheme {
        Column {
            Spacer(modifier = Modifier.size(15.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                content = {
                    itemsIndexed(getMenuList()) { index, item ->
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp),
                            colors = ButtonDefaults.buttonColors(Pink80),
                            shape = RoundedCornerShape(15),
                            onClick = {
                                when (index) {
                                    0 -> {
                                        myNavController.navigate("text_recognizer")
                                    }

                                    1 -> {
                                        myNavController.navigate("detect_faces")
                                    }

                                    2 -> {
                                        myNavController.navigate("scan_barcodes")
                                    }

                                    3 -> {
                                        myNavController.navigate("label_images")
                                    }
                                }
                            })
                        {
                            Text(text = item)
                        }
                        Spacer(modifier = Modifier.size(15.dp))
                    }
                })
        }
    }
}

fun getMenuList(): ArrayList<String> {
    val list = ArrayList<String>()
    list.add("Text Recognize")
    list.add("Detect Faces")
    list.add("Scan Barcodes")
    list.add("Label Images")
    return list
}