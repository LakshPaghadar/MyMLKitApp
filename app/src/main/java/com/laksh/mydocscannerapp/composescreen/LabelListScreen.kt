package com.laksh.mydocscannerapp.composescreen

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.util.Arrays


@Composable
fun LabelListScreen(myNavController: NavController, labels_list: String?, image: String?) {
    Scaffold(
        topBar = {
            BaseTopAppBar(title = "Label Images") {
                myNavController.popBackStack()
            }
        },
        content = { it ->
            //val string = Arrays.asList(labels_list)
            val string = labels_list?.split(",")

            Log.e("ARRAY_LIST 3 ", "$string || $image")

            val a = string.toString().split(",")
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                ) {
                    itemsIndexed(items = a) { count, it ->
                        if (it.contains("[")){
                            val it1=it.removeRange(0,1)
                            Text(text = "$count) $it1")
                        } else if (it.contains("]")){
                            val it1=it.removeRange(it.lastIndex,it.lastIndex+1)
                            Text(text = "$count) $it1")
                        } else {
                            Text(text = "$count) $it")
                        }
                    }
                }
            }
        }
    )

}
