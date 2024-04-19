package com.laksh.mydocscannerapp.composescreen

import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.laksh.mydocscannerapp.R


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TextRecognizer(/*myNavController: NavController*/) {
    //CameraPreview()

    Scaffold(
        topBar = {
            BaseTopAppBar(title = "Text Recognizer")
        },
        content = { it ->
            ScrollContent(it)
        }
    )
}

@Composable
private fun ScrollContent(paddingValues: PaddingValues) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "TextRecognizer")

    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermission() {
    val cameraPermissionState: PermissionState =
        rememberPermissionState(android.Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        //CameraPreview()
    } else {

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CameraScreen() {
    CameraContent()
}

@Composable
fun CameraContent() {
    val context= LocalContext.current
    val lifecycleOwner= LocalLifecycleOwner.current
    val camController= remember {
        LifecycleCameraController(context)
    }
    Scaffold { paddingValues ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            factory = { context ->
                PreviewView(context).apply {
                    layoutParams=LinearLayout.LayoutParams(FILL_PARENT,FILL_PARENT)
                    scaleType = PreviewView.ScaleType.FILL_START
                    setBackgroundColor(context.getColor(R.color.teal_700))
                }.also { previewView ->
                    previewView.controller=camController
                    camController.bindToLifecycle(lifecycleOwner)
                }
            })
    }

}

