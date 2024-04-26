package com.laksh.mydocscannerapp.composescreen

import android.Manifest
import android.graphics.Rect
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.laksh.mydocscannerapp.ui.theme.Pink80
import com.laksh.mydocscannerapp.utils.FaceDetectionUnit

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DetectFaces() {
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
        com.laksh.mydocscannerapp.face.CheckPermission()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermission() {
    val cameraPermissionState: PermissionState =
        rememberPermissionState(Manifest.permission.CAMERA)
    if (cameraPermissionState.status.isGranted) {
        com.laksh.mydocscannerapp.face.CameraContent()
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
    val detectedFaces = remember { mutableStateOf(emptyList<Rect>()) }
    val canvasSize = remember { mutableStateOf(androidx.compose.ui.geometry.Size(0.0f, 0.0f)) }
    val camProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    Scaffold(
        modifier = Modifier.fillMaxSize(),

        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
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
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    factory = { context ->
                        val previewView = PreviewView(context)
                        val executor = ContextCompat.getMainExecutor(context)
                        camProviderFuture.addListener({
                            val camProvider = camProviderFuture.get()
                            val preview = androidx.camera.core.Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }
                            val camSelector = CameraSelector.Builder()
                                .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                                .build()

                            val imageAnalysis = ImageAnalysis.Builder()
                                .setTargetResolution(Size(previewView.width, previewView.height))
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .setImageQueueDepth(10)
                                .build()
                                .apply {
                                    setAnalyzer(executor, FaceDetectionUnit { dFaces ->
                                        //detectedFaces.value = dFaces
                                    })
                                }
                            camProvider.unbindAll()
                            camProvider.bindToLifecycle(
                                lifecycleOwner,
                                camSelector,
                                preview,
                                imageAnalysis
                            )

                        }, executor)
                        previewView
                    }
                )
                FaceDetectionCanvas(detectedFaces, canvasSize)
            }
        }
    }
}


@Composable
fun FaceDetectionCanvas(
    detectedFaces: MutableState<List<Rect>>,
    canvasSize: MutableState<androidx.compose.ui.geometry.Size>,
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = Color.Black.copy(0.5f), size = size)
        canvasSize.value = size
        //Log.e("SIZE_Canvas","height : ${size.height} & width : ${size.width}")
    }
    FaceDetectionOverlay(detectedFaces.value, canvasSize.value)
}

@Composable
fun FaceDetectionOverlay(detectedFaces: List<Rect>, canvasSize: androidx.compose.ui.geometry.Size) {
    Box(
        modifier = Modifier
            .size(300.dp, 400.dp),
        contentAlignment = Alignment.Center
    ) {
        for (faceRect in detectedFaces) {
            val scaledX = faceRect.left * canvasSize.width / 1000f // Scale based on canvas size
            val scaledY = faceRect.top * canvasSize.height / 1000f // Scale based on canvas size
            val scaledWidth = (faceRect.right - faceRect.left) * canvasSize.width / 1000f
            val scaledHeight = (faceRect.bottom - faceRect.top) * canvasSize.height / 1000f

            Box(
                modifier = Modifier
                    //.fillMaxSize()
                    .offset(x = scaledX.dp, y = scaledY.dp) // Use scaled positions
                    .size(width = scaledWidth.dp, height = scaledHeight.dp)
                    .background(Color.Red.copy(alpha = 0.5f)) // Semi-transparent red highlight
            )
        }
    }
}

