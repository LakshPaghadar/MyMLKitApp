package com.laksh.mydocscannerapp.composescreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun NoPermissionScreen(shouldShow: Boolean, onRequestPermission:  () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (shouldShow) {
            Text(text = "Please grant permission to use camera")
            Button(onClick = {
                onRequestPermission.invoke()

            }) {
                Text(text = "Grant Permission")
            }
        } else {
            Text(text = "Please grant permission from settings to use camera")
        }
    }
}