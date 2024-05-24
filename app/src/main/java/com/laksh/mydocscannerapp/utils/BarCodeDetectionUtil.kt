package com.laksh.mydocscannerapp.utils

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class BarCodeDetectionUtil(var onBarcodeDetect:(type:String,value:String)->Unit): ImageAnalysis.Analyzer {
    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            val result = scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    processBarCode(barcodes)
                }
                .addOnFailureListener {

                }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    fun getInformationFromBarcode(imageProxy: ImageProxy){
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val scanner = BarcodeScanning.getClient()

            val result = scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    processBarCode(barcodes)
                }
                .addOnFailureListener {
                    Log.e("SCAN_BARCODE",it.message.toString())
                }
        }
    }
    private fun processBarCode(barcodes: MutableList<Barcode>) {
        for (barcode in barcodes) {
            val bounds = barcode.boundingBox
            val corners = barcode.cornerPoints

            val rawValue = barcode.rawValue

            val valueType = barcode.valueType
            Log.e("SCAN_BARCODE VALUE",valueType.toString())
            Log.e("SCAN_BARCODE DISPLAY",barcode.displayValue.toString())
            // See API reference for complete list of supported types
            when (valueType) {
                Barcode.TYPE_WIFI -> {
                    val ssid = barcode.wifi!!.ssid
                    val password = barcode.wifi!!.password
                    val type = barcode.wifi!!.encryptionType
                    onBarcodeDetect.invoke("TYPE_WIFI",barcode.displayValue.toString())
                }
                Barcode.TYPE_URL -> {
                    val title = barcode.url!!.title
                    val url = barcode.url!!.url
                    onBarcodeDetect.invoke("TYPE_URL",url.toString())
                }
                Barcode.TYPE_PRODUCT->{
                    Log.e("SCAN_BARCODE TYPE_PRODUCT",rawValue.toString())
                    onBarcodeDetect.invoke("TYPE_PRODUCT",barcode.displayValue.toString())
                }
                Barcode.TYPE_TEXT->{
                    Log.e("SCAN_BARCODE TYPE_TEXT",rawValue.toString())
                    onBarcodeDetect.invoke("TYPE_TEXT",barcode.displayValue.toString())
                }
                Barcode.TYPE_UNKNOWN->{
                    Log.e("SCAN_BARCODE TYPE_UNKNOWN",rawValue.toString())
                    onBarcodeDetect.invoke("TYPE_UNKNOWN",barcode.displayValue.toString())
                }
                else ->{
                    Log.e("SCAN_BARCODE ELSE",rawValue.toString())
                    onBarcodeDetect.invoke("TYPE_UNKNOWN",barcode.displayValue.toString())
                }
            }
        }
    }
}