package com.laksh.mydocscannerapp.composescreen

import android.Manifest
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.laksh.mydocscannerapp.R
import com.laksh.mydocscannerapp.ui.theme.Pink80
import com.laksh.mydocscannerapp.utils.TextRecoImageAnalyzer


@Composable
fun TextRecognizer(myNavController: NavController) {
    Scaffold(
        topBar = {
            BaseTopAppBar(title = "Text Recognizer") {
                myNavController.popBackStack()
            }
        },
        content = { it ->
            ScrollContent(it, myNavController)
        }
    )
}

@Composable
private fun ScrollContent(paddingValues: PaddingValues, myNavController: NavController) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CheckPermission(myNavController)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermission(myNavController: NavController) {
    val cameraPermissionState: PermissionState =
        rememberPermissionState(Manifest.permission.CAMERA)
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {

        }

    LaunchedEffect(key1 = launcher) {
        if (!cameraPermissionState.status.isGranted) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (cameraPermissionState.status.isGranted) {
        CameraContent(myNavController)
    } else {
        NoPermissionScreen(shouldShow = false) {

        }
    }
}

@Composable
fun CameraContent(myNavController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val camController = remember {
        LifecycleCameraController(context)
    }
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    camController.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                TextRecoImageAnalyzer(object : (String) -> Unit {
                                    override fun invoke(p1: String) {
                                        myNavController.navigate("scanned_text/$p1")
                                        Toast.makeText(context, p1, Toast.LENGTH_SHORT).show()
                                    }
                                }).getTextFromImageCapture(image)
                                super.onCaptureSuccess(image)
                            }

                            override fun onError(exception: ImageCaptureException) {
                                super.onError(exception)
                            }
                        }
                    )
                },
                containerColor = Pink80
            ) {
                Icon(Icons.Filled.Add, "")
            }
        }
    ) { paddingValues ->
        Row {
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 300)
                        scaleType = PreviewView.ScaleType.FILL_START
                        setBackgroundColor(context.getColor(R.color.teal_700))
                    }.also { previewView ->
                        camController.bindToLifecycle(lifecycleOwner)
                        previewView.controller = camController
                    }
                })
        }
    }
}
