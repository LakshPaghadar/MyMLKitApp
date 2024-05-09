package com.laksh.mydocscannerapp.utils

import android.media.Image
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions

class ImageLabelUtils(val onLabelDetect: (ArrayList<String>) -> Unit): ImageAnalysis.Analyzer {
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val DELAY_TIME = 1000L
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        scope.launch {
            val mediaImage = imageProxy.image
            val imageRotation =imageProxy.imageInfo.rotationDegrees
            if (mediaImage != null) {
                val image = getImageObjectFromCamImage(mediaImage, imageRotation)
                suspendCoroutine { continuation->
                    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                    Log.e("LABELS","1")
                    labeler.process(image)
                        .addOnSuccessListener { labels->
                            val array= arrayListOf<String>()
                            for (label in labels) {
                                val text = label.text
                                val confidence = label.confidence
                                val index = label.index
                                Log.e("LABELS",text)
                                array.add(text)
                            }
                            onLabelDetect.invoke(array)
                            imageProxy.close()
                        }
                        .addOnCompleteListener {
                            continuation.resume(Unit)
                            imageProxy.close()
                        }
                        .addOnFailureListener {
                            imageProxy.close()
                        }
                }

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
    @OptIn(ExperimentalGetImage::class)
    fun getLabelsFromImageCapture(imageProxy: ImageProxy){
        scope.launch {
            val mediaImage = imageProxy.image
            val imageRotation =imageProxy.imageInfo.rotationDegrees
            if (mediaImage != null) {
                val image = getImageObjectFromCamImage(mediaImage, imageRotation)
                suspendCoroutine { continuation->
                    val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)
                    labeler.process(image)
                        .addOnSuccessListener { labels->
                            val array= arrayListOf<String>()
                            for (label in labels) {
                                val text = label.text
                                val confidence = label.confidence
                                val index = label.index
                                Log.e("LABELS",text)
                                array.add(text)
                            }
                            onLabelDetect.invoke(array)
                            imageProxy.close()
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
}