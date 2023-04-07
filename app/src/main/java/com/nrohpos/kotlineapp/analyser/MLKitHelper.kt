package com.nrohpos.kotlineapp.analyser

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

object MLKitHelper {
    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    fun analyseFor(uri: Uri?, context: Context, callback: (str: String?) -> Unit): Unit {
        if (uri == null) {
            callback.invoke(null)
            return
        }
        val inputImage = InputImage.fromFilePath(context, uri)
        scanner.process(inputImage).addOnSuccessListener { barcodes ->
            if (barcodes.size > 0) {
                val barcode = barcodes.first()
                callback.invoke(barcode.displayValue)
            }
        }.addOnFailureListener {

        }
    }
}