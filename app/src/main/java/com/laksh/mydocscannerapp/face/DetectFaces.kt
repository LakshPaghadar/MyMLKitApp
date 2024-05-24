package com.laksh.mydocscannerapp.face

import android.Manifest
import android.graphics.Color
import android.widget.FrameLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import androidx.wear.compose.material.Button
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.laksh.mydocscannerapp.R
import com.laksh.mydocscannerapp.composescreen.BaseTopAppBar
import com.laksh.mydocscannerapp.composescreen.NoPermissionScreen
import com.laksh.mydocscannerapp.face.camerax.CameraManager
import com.laksh.mydocscannerapp.face.camerax.GraphicOverlay


@Composable
fun DetectFaces(myNavController: NavController) {
    Scaffold(
        topBar = {
            BaseTopAppBar(title = "Face Detection") {
                myNavController.popBackStack()
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
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->

        }

    LaunchedEffect(key1 = launcher) {
        if (!cameraPermissionState.status.isGranted) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (cameraPermissionState.status.isGranted) {
        MainActivityScreen()
    } else {
        NoPermissionScreen(shouldShow = false) {

        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainActivityScreen() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    lateinit var cameraManager: CameraManager
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (frameLayout, button) = createRefs()

        Box(modifier = Modifier
            .fillMaxWidth()
            .constrainAs(frameLayout) {
                top.linkTo(parent.top)
                bottom.linkTo(button.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                height = Dimension.matchParent
            }) {

            AndroidView(factory = {
                FrameLayout(it).apply {
                    val previewView = PreviewView(it).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                    val graphicOverlay = GraphicOverlay(it, attrs = null).apply {
                        setBackgroundColor(Color.argb(0, 0, 0, 0))
                    }

                    cameraManager =
                        CameraManager(context, previewView, lifecycleOwner, graphicOverlay)
                    cameraManager.startCamera()

                    addView(
                        previewView, FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    )
                    addView(
                        graphicOverlay, FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        )
                    )
                }
            }, modifier = Modifier.fillMaxSize())
        }

        Button(
            onClick = {
                cameraManager.changeCameraSelector()
            },
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .constrainAs(button) {
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
                .padding(20.dp)

        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_switch_camera),
                contentDescription = "Switch Camera",
                modifier = Modifier
                    .size(40.dp)
                    .padding(4.dp)

            )
        }
    }
}






