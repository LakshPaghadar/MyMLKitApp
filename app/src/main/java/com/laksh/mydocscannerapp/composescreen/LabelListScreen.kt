package com.laksh.mydocscannerapp.composescreen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun LabelListScreen(labels_list: Array<String>?, image: String?) {
    Log.e("ARRAY_LIST ", "$labels_list || $image")
    Column(modifier = Modifier.fillMaxSize()) {

        if (labels_list != null) {
            LazyColumn {
                itemsIndexed(items = labels_list.toList()) { count, it ->
                    Text(text = "$count) $it")
                }
            }
        } else {
            Text(text = "No data found")
        }
    }
}
