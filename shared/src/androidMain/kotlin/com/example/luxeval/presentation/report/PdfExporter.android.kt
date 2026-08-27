package com.example.luxeval.presentation.report

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

actual class PdfExporter(
    private val context: Context
) {

    actual suspend fun exportToPdf(
        html: String,
        outputFileName: String
    ): String = withContext(Dispatchers.IO) {

        val exportDir = File(
            context.filesDir,
            "exports"
        ).apply {
            mkdirs()
        }

        val outputFile = File(
            exportDir,
            outputFileName
        )

        val pdfDocument = PdfDocument()

        try {
            val pageWidth = 595
            val pageHeight = 842

            val pageInfo = PdfDocument.PageInfo.Builder(
                pageWidth,
                pageHeight,
                1
            ).create()

            val page = pdfDocument.startPage(pageInfo)

            val canvas: Canvas = page.canvas

            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                textSize = 14f
            }

            canvas.drawText(
                "LuxeVal Appraisal Report",
                40f,
                60f,
                paint
            )

            canvas.drawText(
                "Report generated successfully.",
                40f,
                100f,
                paint
            )

            pdfDocument.finishPage(page)

            FileOutputStream(outputFile).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }

        } finally {
            pdfDocument.close()
        }

        outputFile.absolutePath
    }
}