package com.nrohpos.kotlineapp.ui

import android.Manifest
import android.graphics.RectF
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.nrohpos.kotlineapp.analyser.BarcodeAnalyser
import com.nrohpos.kotlineapp.analyser.CameraSetup
import com.nrohpos.kotlineapp.analyser.MLKitHelper
import com.nrohpos.kotlineapp.extension.navigateToAppSettings
import java.util.concurrent.Executors


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen() {
    val permissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )
    val context = LocalContext.current
    KotlinPermission(
        permissionStatus = permissionState.status,
        permissionNotAvailableContent = {
            LaunchedEffect(
                key1 = permissionState.status.isGranted,
                block = {
                    permissionState.launchPermissionRequest()
                },
            )
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "App is need for permission. Allowed permission in setting , By Clicked here",
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            context.navigateToAppSettings()
                        },
                    textAlign = TextAlign.Center,
                )
            }
        },
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            var isShowGallery by remember { mutableStateOf(false) }
            var imageUri by remember { mutableStateOf<Uri?>(null) }
            if (imageUri != null) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = rememberAsyncImagePainter(model = imageUri),
                    contentDescription = "Captured image"
                )
                Button(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    onClick = {
                        imageUri = null
                    }
                ) {
                    Text("clear image")
                }
            } else {
                if (isShowGallery) {
                    OpenGallery(
                        onImageUri = {
                            imageUri = it
                            MLKitHelper.analyseFor(imageUri, context, callback = {
                                Toast.makeText(context, "Barcode found ${it}", Toast.LENGTH_LONG)
                                    .show()
                            })
                            isShowGallery = false
                        },
                    )
                } else {
                    AndroidView(
                        { context ->
                            CameraSetup.init(context)
                        },
                        modifier = Modifier
                            .fillMaxSize()
                            .drawWithContent {
                                val overlayWidth = size.width
                                val overlayHeight = size.height
                                val boxWidth = overlayWidth * 0.65
                                val boxHeight = overlayHeight * 0.35
                                val cx = overlayWidth / 2
                                val cy = overlayHeight / 2
                                val rectF = RectF(
                                    (cx - boxWidth / 2).toFloat(),
                                    (cy - boxHeight / 2).toFloat(),
                                    (cx + boxWidth / 2).toFloat(),
                                    (cy + boxHeight / 2).toFloat()
                                )
                                val scrimPaint: Paint = Paint().apply {
                                    color = Color(0xFF99000000)
                                }
                                val boxPaint: Paint = Paint().apply {
                                    color = Color.White
                                    style = PaintingStyle.Stroke
                                    strokeWidth = 4f
                                }
                                val eraserPaint: Paint = Paint().apply {
                                    strokeWidth = boxPaint.strokeWidth
                                    blendMode = BlendMode.Clear
                                }

                                drawIntoCanvas { canvas ->
                                    drawContent()
                                    canvas.drawPath(
                                        Path(),
                                        Paint().apply {
                                            strokeWidth = 4f
                                            color = Color.White
                                        },
                                    )
                                    canvas.drawRect(
                                        left = 0f,
                                        top = 0f,
                                        right = overlayWidth,
                                        bottom = overlayHeight,
                                        paint = scrimPaint
                                    )
                                    eraserPaint.style = PaintingStyle.Fill
                                    canvas.drawRoundRect(
                                        left = rectF.left,
                                        top = rectF.top,
                                        right = rectF.right,
                                        bottom = rectF.bottom,
                                        8f,
                                        8f,
                                        eraserPaint
                                    )
                                    eraserPaint.style = PaintingStyle.Stroke
                                    canvas.drawRoundRect(
                                        left = rectF.left,
                                        top = rectF.top,
                                        right = rectF.right,
                                        bottom = rectF.bottom,
                                        8f,
                                        8f,
                                        eraserPaint
                                    )
                                    canvas.drawRoundRect(
                                        left = rectF.left,
                                        top = rectF.top,
                                        right = rectF.right,
                                        bottom = rectF.bottom,
                                        8f,
                                        8f,
                                        eraserPaint
                                    )
                                    // draw border
                                    val width = size.width
                                    val height = size.height
                                    val mWidth = cx - boxWidth / 2
                                    val mHeight = cy - boxHeight / 2
                                    val lineHeight = 100f

                                    val path = Path()
                                    path.moveTo((width - mWidth).toFloat(), (mHeight).toFloat())
                                    path.lineTo(
                                        (width - mWidth).toFloat(),
                                        (mHeight + lineHeight).toFloat()
                                    )

                                    path.moveTo((width - mWidth).toFloat(), (mHeight).toFloat())
                                    path.lineTo(
                                        (width - mWidth).toFloat() - lineHeight,
                                        (mHeight).toFloat()
                                    )

                                    path.moveTo(
                                        (width - mWidth).toFloat(),
                                        (height - mHeight).toFloat()
                                    )
                                    path.lineTo(
                                        (width - mWidth).toFloat() - lineHeight,
                                        (height - mHeight).toFloat()
                                    )

                                    path.moveTo(
                                        (width - mWidth).toFloat(),
                                        (height - mHeight).toFloat()
                                    )
                                    path.lineTo(
                                        (width - mWidth).toFloat(),
                                        ((height - mHeight) - lineHeight).toFloat()
                                    )

                                    path.moveTo((mWidth).toFloat(), (mHeight).toFloat())
                                    path.lineTo(
                                        (mWidth).toFloat(),
                                        (mHeight + lineHeight).toFloat()
                                    )

                                    path.moveTo((mWidth).toFloat(), (mHeight).toFloat())
                                    path.lineTo(
                                        (mWidth).toFloat() + lineHeight,
                                        (mHeight).toFloat()
                                    )

                                    path.moveTo((mWidth).toFloat(), (height - mHeight).toFloat())
                                    path.lineTo(
                                        (mWidth).toFloat(),
                                        ((height - mHeight) - lineHeight).toFloat()
                                    )

                                    path.moveTo((mWidth).toFloat(), (height - mHeight).toFloat())
                                    path.lineTo(
                                        (mWidth).toFloat() + lineHeight,
                                        ((height - mHeight)).toFloat()
                                    )
                                    path.close()
                                    canvas.drawPath(path, boxPaint)
                                    // full frame view with stroke view
                                //                                    canvas.drawRoundRect(
//                                        left = rectF.left,
//                                        top = rectF.top,
//                                        right = rectF.right,
//                                        bottom = rectF.bottom,
//                                        8f,
//                                        8f,
//                                        boxPaint
//                                    )
                                }
                            }
                    )
                    Text(
                        "Upload QR",
                        modifier = Modifier
                            .drawWithCache {
                                onDrawBehind {
                                    drawRoundRect(
                                        Brush.linearGradient(
                                            listOf(
                                                Color.White,
                                                Color.Cyan
                                            )
                                        ),
                                        cornerRadius = CornerRadius(16.dp.toPx())
                                    )
                                }
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .align(Alignment.BottomCenter)
                            .clickable {
                                isShowGallery = true
                            },
                    )
                }
            }
        }
    }
}