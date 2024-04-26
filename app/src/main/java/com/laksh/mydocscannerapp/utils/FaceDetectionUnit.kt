package com.laksh.mydocscannerapp.utils

import android.graphics.Rect
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.Composable
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FaceDetectionUnit(/*val onFaceDetectionRect: (list:ArrayList<Rect>)->Unit,*/val onFaceDetection: (list:ArrayList<Face>)->Unit) : ImageAnalysis.Analyzer {
    var highAccuracyOpts = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
        //.setMinFaceSize(0.2f)
        .enableTracking()
        .build()
    val detector = FaceDetection.getClient(highAccuracyOpts)
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            detector.process(image)
                .addOnSuccessListener { faces ->
                    getFaceData(faces)
                    val list= arrayListOf<Rect>()
                    faces.forEach {
                        val rect = it.boundingBox
                        list.add(Rect(rect.left, rect.top, rect.right, rect.bottom))
                        //Log.e("SIZE_Canvas","FACE_DETECTION = right : ${rect.right} & left : ${rect.left} & bottom : ${rect.bottom} & top : ${rect.top}")
                    }
                    onFaceDetection.invoke(faces as ArrayList<Face>)
                    imageProxy.close()
                }
                .addOnFailureListener { e ->
                    imageProxy.close()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
    @OptIn(ExperimentalGetImage::class)
    fun getFacesFromImageCapture(imageProxy: ImageProxy){
        scope.launch {
            val mediaImage = imageProxy.image
            val imageRotation =imageProxy.imageInfo.rotationDegrees
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageRotation)
                detector.process(image)
                    .addOnSuccessListener { faces ->
                        getFaceData(faces)
                        val list= arrayListOf<Rect>()
                        faces.forEach {
                            val rect = it.boundingBox
                            list.add(Rect(rect.left, rect.top, rect.right, rect.bottom))
                            //Log.e("SIZE_Canvas","FACE_DETECTION = right : ${rect.right} & left : ${rect.left} & bottom : ${rect.bottom} & top : ${rect.top}")
                        }
                        onFaceDetection.invoke(faces as ArrayList<Face>)
                        imageProxy.close()
                    }
                    .addOnFailureListener { e ->
                        imageProxy.close()
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
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

    private fun getFaceData(faces: MutableList<Face>) {
        for (face in faces) {
            val bounds = face.boundingBox
            val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
            val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

            // If landmark detection was enabled (mouth, ears, eyes, cheeks, and
            // nose available):
            val leftEar = face.getLandmark(FaceLandmark.LEFT_EAR)
            leftEar?.let {
                val leftEarPos = leftEar.position
                    Log.e("SIZE_Canvas","FACE_LANDMARK = right : ${leftEar.position.x} & left : ${leftEar.position.x} ")
            }


            // If contour detection was enabled:
            val leftEyeContour = face.getContour(FaceContour.LEFT_EYE)?.points
            val upperLipBottomContour =
                face.getContour(FaceContour.UPPER_LIP_BOTTOM)?.points

            // If classification was enabled:
            if (face.smilingProbability != null) {
                val smileProb = face.smilingProbability
            }
            if (face.rightEyeOpenProbability != null) {
                val rightEyeOpenProb = face.rightEyeOpenProbability
            }

            // If face tracking was enabled:
            if (face.trackingId != null) {
                val id = face.trackingId
            }
        }
    }

}