package com.laksh.mydocscannerapp.composescreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun BarcodeDetailsScreen(myNavController: NavController,type:String?,value:String?) {
    Scaffold(
        topBar = {
            BaseTopAppBar(title = "Barcode Value") {
                myNavController.popBackStack()
            }
        },
        content = { it ->
            Box(modifier = Modifier.padding(it)) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 20.dp, horizontal = 20.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(text = "Type : $type")
                    Text(text = "Value : $value")
                }
            }
        }
    )
}