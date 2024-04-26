package com.laksh.mydocscannerapp.face

import android.Manifest
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.face.Face
import com.laksh.mydocscannerapp.R
import com.laksh.mydocscannerapp.composescreen.BaseTopAppBar
import com.laksh.mydocscannerapp.composescreen.NoPermissionScreen
import com.laksh.mydocscannerapp.ui.theme.Pink80
import com.laksh.mydocscannerapp.utils.FaceDetectionUnit


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetectFaces2() {
    Scaffold(
        topBar = {
            BaseTopAppBar(title = "Detect Faces") {

            }
        },
        content = { it ->
            ScrollContent(it)
        }
    )
}

@Composable
private fun ScrollContent(paddingValues: PaddingValues/*,myNavController: NavController*/) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CheckPermission()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermission() {
    val cameraPermissionState: PermissionState =
        rememberPermissionState(Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        CameraContent()
    } else if (!cameraPermissionState.status.shouldShowRationale) {
        Log.e("PERMISSION", cameraPermissionState.status.shouldShowRationale.toString())
        NoPermissionScreen(cameraPermissionState.status.shouldShowRationale) {
            cameraPermissionState.launchPermissionRequest()
        }
    } else {
        NoPermissionScreen(cameraPermissionState.status.shouldShowRationale) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
}

@Composable
fun CameraContent() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val camController = remember {
        LifecycleCameraController(context)
    }
    camController.cameraSelector=CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
        .build()
    val detectedFaces = remember { mutableStateOf(emptyList<Face>()) }
    val isImageTaken = remember { mutableStateOf(false) }
    lateinit var imageBitmap:ImageBitmap
    val canvasSize = remember { mutableStateOf(androidx.compose.ui.geometry.Size(0.0f, 0.0f)) }
    val camProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    camController.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                imageBitmap=image.toBitmap().asImageBitmap()
                                FaceDetectionUnit {
                                    detectedFaces.value=it
                                    isImageTaken.value=true
                                }.getFacesFromImageCapture(image)
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(300.dp, 400.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isImageTaken.value){
                    CanvasDraw(detectedFaces.value,imageBitmap)
                } else {
                    AndroidView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        factory = { context ->
                            PreviewView(context).apply {
                                layoutParams =
                                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300)
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
    }
}


@Composable
fun CanvasDraw(faces: List<Face>, imageBitmap:ImageBitmap){
        Canvas(modifier = Modifier.size(300.dp, 400.dp)) {
            // Your image bitmap
            val imageWidth = imageBitmap.width
            val imageHeight = imageBitmap.height

            drawImage(imageBitmap)

            faces.forEach { face ->
                val landmarks = face.allLandmarks
                landmarks.forEach { landmark ->
                    val normalizedX = landmark.position.x
                    val normalizedY = landmark.position.y
                    val x = normalizedX * imageWidth
                    val y = normalizedY * imageHeight
                    drawCircle(
                        color = Color.Red,
                        radius = 5f,
                        center = Offset(x, y)
                    )
                }
            }
        }
}


