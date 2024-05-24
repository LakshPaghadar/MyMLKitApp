import android.Manifest
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.laksh.mydocscannerapp.R
import com.laksh.mydocscannerapp.composescreen.BaseTopAppBar
import com.laksh.mydocscannerapp.composescreen.NoPermissionScreen
import com.laksh.mydocscannerapp.ui.theme.Pink80
import com.laksh.mydocscannerapp.utils.BarCodeDetectionUtil

@Composable
fun ScanBarcodes(myNavController: NavController) {
    Scaffold(
        topBar = {
            BaseTopAppBar(title = "Barcode Scanner") {
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
        CheckPermission1(myNavController = myNavController)
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermission1(myNavController: NavController) {
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
        CameraContent1(myNavController)
    } else {
        NoPermissionScreen(shouldShow = false) {

        }

    }
}

@Composable
fun CameraContent1(myNavController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val camController = remember {
        LifecycleCameraController(context)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    camController.takePicture(
                        ContextCompat.getMainExecutor(context),
                        object : ImageCapture.OnImageCapturedCallback() {
                            override fun onCaptureSuccess(image: ImageProxy) {
                                Log.e("SCAN_BARCODE", image.toString())
                                BarCodeDetectionUtil{ type, value ->
                                    myNavController.navigate("barcode_details/${type}/${value}")
                                }.getInformationFromBarcode(image)
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
                .fillMaxSize().padding(vertical = 100.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            AndroidView(
                modifier = Modifier
                    .height(250.dp)
                    .width(300.dp)
                    .padding(paddingValues),
                factory = { context ->
                    PreviewView(context).apply {
                        layoutParams =
                            LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300)
                        scaleType = PreviewView.ScaleType.FILL_START
                        setBackgroundColor(context.getColor(R.color.teal_700))
                    }.also { previewView ->
                        camController.bindToLifecycle(lifecycleOwner)
                        camController.setZoomRatio(0.1f)
                        previewView.controller = camController
                    }
                })
        }
    }
}