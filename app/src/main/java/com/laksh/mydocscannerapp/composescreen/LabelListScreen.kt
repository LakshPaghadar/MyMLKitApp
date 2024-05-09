package com.laksh.mydocscannerapp.composescreen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import java.util.Arrays





@Composable
fun LabelListScreen(labels_list: String?, image: String?) {
    val string = Arrays.asList(labels_list)
    Log.e("ARRAY_LIST 3 ", "$string || $image")

    val a=string.toString().split(",")
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            itemsIndexed(items = a.toList()) { count, it ->
                Text(text = "$count) $it")
            }
        }
    }
}
