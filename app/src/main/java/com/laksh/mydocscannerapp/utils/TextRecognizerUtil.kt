package com.laksh.mydocscannerapp.utils

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata

class TextRecognizerUtil : ImageAnalysis.Analyzer {
    private fun degreesToFirebaseRotation(degrees: Int): Int = when(degrees) {
        0 -> FirebaseVisionImageMetadata.ROTATION_0
        90 -> FirebaseVisionImageMetadata.ROTATION_90
        180 -> FirebaseVisionImageMetadata.ROTATION_180
        270 -> FirebaseVisionImageMetadata.ROTATION_270
        else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
    }

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        val imageRotation = degreesToFirebaseRotation(180)
        if (mediaImage != null) {
            val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
            // Pass image to an ML Kit Vision API
            // ...
        }
    }
    /*@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
   override fun analyze(imageProxy: ImageProxy?, degrees: Int) {
       val mediaImage = imageProxy?.image
       val imageRotation = degreesToFirebaseRotation(degrees)
       if (mediaImage != null) {
           val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
           // Pass image to an ML Kit Vision API
           // ...
       }
   }*/
}