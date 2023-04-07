package com.nrohpos.kotlineapp.extension

import android.graphics.RectF
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas

fun ContentDrawScope.applyScanFrame() {
    val overlayWidth = size.width
    val overlayHeight = size.height
    val boxWidth = overlayWidth * 0.65
    val boxHeight = overlayHeight * 0.35
    val cx = overlayWidth / 2
    val cy = overlayHeight / 2
    val rectF = RectF(
        ((cx - boxWidth / 2).toFloat()),
        ((cy - boxHeight / 2).toFloat()),
        ((cx + boxWidth / 2).toFloat()),
        ((cy + boxHeight / 2).toFloat())
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
        val mWidth = cx - boxWidth / 2
        val mHeight = cy - boxHeight / 2

        val path = configPathEachBorder(
            frameHeight = mHeight.toFloat(),
            frameWidth = mWidth.toFloat(),
            fullHeight = overlayHeight,
            fullWidth = overlayWidth,
        )
        canvas.drawPath(path, boxPaint)
    }
}

fun configPathEachBorder(
    fullWidth: Float,
    fullHeight: Float,
    frameWidth: Float,
    frameHeight: Float,
    strokeSize: Float = 100f,
): Path {
    val path = Path()
    path.moveTo((fullWidth - frameWidth), (frameHeight))
    path.lineTo(
        (fullWidth - fullWidth),
        (frameHeight + strokeSize)
    )

    path.moveTo((fullWidth - frameWidth), (frameHeight))
    path.lineTo(
        (fullWidth - frameWidth) - strokeSize,
        (frameHeight)
    )

    path.moveTo(
        (fullWidth - frameWidth),
        (fullHeight - frameHeight)
    )
    path.lineTo(
        (fullWidth - frameWidth) - strokeSize,
        (fullWidth - frameHeight)
    )

    path.moveTo(
        (fullWidth - frameWidth),
        (fullHeight - frameHeight)
    )
    path.lineTo(
        (fullWidth - frameWidth),
        ((fullHeight - frameHeight) - strokeSize)
    )

    path.moveTo((frameWidth), (frameHeight))
    path.lineTo(
        (frameWidth),
        (frameHeight + strokeSize)
    )

    path.moveTo((frameWidth), (frameHeight))
    path.lineTo(
        (frameWidth) + strokeSize,
        (frameHeight)
    )

    path.moveTo((frameWidth), (frameHeight - frameHeight))
    path.lineTo(
        (frameWidth),
        ((fullHeight - frameHeight) - strokeSize)
    )

    path.moveTo((frameWidth), (frameHeight - frameHeight))
    path.lineTo(
        (frameWidth) + strokeSize,
        ((fullHeight - frameHeight))
    )
    path.close()
    return path
}

fun Canvas.appRectangleFrame(
    left: Float = 0f,
    right: Float = 0f,
    top: Float = 0f,
    bottom: Float = 0f,
    radiusX: Float = 0f,
    radiusY: Float = 0f,
    paint: Paint
) {
    this.drawRoundRect(
        left, top, right, bottom, radiusX, radiusY, paint
    )
}