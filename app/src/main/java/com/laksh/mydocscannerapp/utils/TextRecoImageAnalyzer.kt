package com.laksh.mydocscannerapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.android.gms.vision.text.TextRecognizer
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizerOptionsInterface
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import java.nio.ByteBuffer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TextRecoImageAnalyzer(val onTextDetectUpdate: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val DELAY_TIME = 1000L
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private fun degreesToFirebaseRotation(degrees: Int): Int {
        return when (degrees) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
        }
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        scope.launch {
            val mediaImage = imageProxy.image
            val imageRotation =imageProxy.imageInfo.rotationDegrees
            if (mediaImage != null) {
                val image = getImageObjectFromCamImage(mediaImage, imageRotation)
                suspendCoroutine { continuation->
                    textRecognizer.process(image)
                        .addOnSuccessListener {
                            val text=it.text
                            if (text.isNotBlank()){
                                onTextDetectUpdate(text)
                            }
                        }
                        .addOnCompleteListener {
                            continuation.resume(Unit)
                        }
                        .addOnFailureListener {

                        }
                }
                delay(DELAY_TIME)
            } else {
                imageProxy.close()
                return@launch
            }
        }.invokeOnCompletion {
            it?.printStackTrace()
            imageProxy.close()
        }
    }
    @OptIn(ExperimentalGetImage::class)
    fun getTextFromImageCapture(imageProxy: ImageProxy){
        scope.launch {
            val mediaImage = imageProxy.image
            val imageRotation =imageProxy.imageInfo.rotationDegrees
            if (mediaImage != null) {
                val image = getImageObjectFromCamImage(mediaImage, imageRotation)
                suspendCoroutine { continuation->
                    textRecognizer.process(image)
                        .addOnSuccessListener {
                            val text=it.text
                            if (text.isNotBlank()){
                                onTextDetectUpdate(text)
                            }
                        }
                        .addOnCompleteListener {
                            continuation.resume(Unit)
                        }
                        .addOnFailureListener {

                        }
                }
                delay(DELAY_TIME)
            } else {
                imageProxy.close()
                return@launch
            }
        }.invokeOnCompletion {
            it?.printStackTrace()
            imageProxy.close()
        }
    }

    private fun getImageObjectFromCamImage(mediaImage: Image, rotation: Int): InputImage {
        return InputImage.fromMediaImage(mediaImage, rotation)
    }

    private fun getImageObjectFromBitmap(bitmap: Bitmap, rotation: Int): InputImage {
        return InputImage.fromBitmap(bitmap,rotation)
    }

    private fun getImageObjectFromBytes(buffer: ByteArray, rotation: Int): InputImage {
        val image = InputImage
            .fromByteArray(
                buffer,
                480,
                360,// 480x360 is typically sufficient for
                rotation, // image recognition
                InputImage.IMAGE_FORMAT_NV21
            )
        return image
    }

    private fun getImageObjectFromUri(context: Context, uri: Uri): InputImage? {
        val image: InputImage
        return try {
            image = InputImage.fromFilePath(context, uri)
            image
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}