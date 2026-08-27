//package com.example.luxeval.presentation.report
//
//import android.content.Context
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.pdf.PdfDocument
//import android.os.CancellationSignal
//import android.os.ParcelFileDescriptor
//import android.print.PageRange
//import android.print.PrintAttributes
//import android.print.PrintDocumentAdapter
//import android.print.PrintDocumentInfo
//import android.view.View
//import android.webkit.WebView
//import android.webkit.WebViewClient
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.suspendCancellableCoroutine
//import kotlinx.coroutines.withContext
//import java.io.File
//import java.io.FileOutputStream
//import kotlin.coroutines.resume
//import kotlin.coroutines.resumeWithException

package com.example.luxeval.presentation.report

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
actual class PdfExporter(private val context: Context) {

    actual suspend fun exportToPdf(html: String, outputFileName: String): String {
        val activity = CurrentActivityHolder.current
            ?: error("No foreground activity available to render PDF")

        val bitmap = withContext(Dispatchers.Main) {
            renderHtmlToBitmap(activity, html, pageWidthPx = 595)
        }
        return withContext(Dispatchers.IO) {
            writeBitmapToPdf(bitmap, outputFileName)
        }
    }

    private suspend fun renderHtmlToBitmap(
        activity: Activity,
        html: String,
        pageWidthPx: Int
    ): Bitmap = suspendCancellableCoroutine { cont ->
        val webView = WebView(activity)
        webView.settings.javaScriptEnabled = true
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        // Attach off-screen but real, so rendering/JS actually complete.
        val root = activity.window.decorView as ViewGroup
        val params = FrameLayout.LayoutParams(pageWidthPx, 10) // will be resized after load
        webView.layoutParams = params
        webView.alpha = 0f
        webView.translationX = -10000f // move off-screen instead of relying on alpha alone
        root.addView(webView)

        fun cleanupAndResume(bmp: Bitmap) {
            root.removeView(webView)
            if (cont.isActive) cont.resume(bmp)
        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                view.postDelayed({
                    val density = view.resources.displayMetrics.density
                    // contentHeight is synchronous and works reliably once attached — no JS round-trip needed.
                    val contentHeightPx = (view.contentHeight * density).toInt().coerceAtLeast(1)

                    view.measure(
                        View.MeasureSpec.makeMeasureSpec(pageWidthPx, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(contentHeightPx, View.MeasureSpec.EXACTLY)
                    )
                    view.layout(0, 0, pageWidthPx, contentHeightPx)

                    val bmp = Bitmap.createBitmap(pageWidthPx, contentHeightPx, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bmp)
                    canvas.drawColor(Color.WHITE)
                    view.draw(canvas)

                    cleanupAndResume(bmp)
                }, 300)
            }
        }

        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)

        cont.invokeOnCancellation { root.removeView(webView) }
    }

    private fun writeBitmapToPdf(bitmap: Bitmap, outputFileName: String): String {
        val exportDir = File(context.filesDir, "exports").apply { mkdirs() }
        val outputFile = File(exportDir, outputFileName)
        val pageWidth = bitmap.width
        val pageHeightPerPage = 842

        val pdfDocument = PdfDocument()
        try {
            var y = 0
            var pageNum = 1
            while (y < bitmap.height) {
                val sliceHeight = minOf(pageHeightPerPage, bitmap.height - y)
                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, sliceHeight, pageNum).create()
                val page = pdfDocument.startPage(pageInfo)
                page.canvas.drawBitmap(
                    bitmap,
                    Rect(0, y, pageWidth, y + sliceHeight),
                    Rect(0, 0, pageWidth, sliceHeight),
                    null
                )
                pdfDocument.finishPage(page)
                y += sliceHeight
                pageNum++
            }
            FileOutputStream(outputFile).use { pdfDocument.writeTo(it) }
        } finally {
            pdfDocument.close()
        }
        return outputFile.absolutePath
    }
}
//actual class PdfExporter(private val context: Context) {
//
//    actual suspend fun exportToPdf(html: String, outputFileName: String): String {
//        // WebView must be created/driven on the main thread.
//        val bitmap = withContext(Dispatchers.Main) {
//            renderHtmlToBitmap(html, pageWidthPx = 595)
//        }
//        return withContext(Dispatchers.IO) {
//            writeBitmapToPdf(bitmap, outputFileName)
//        }
//    }
//
//    private suspend fun renderHtmlToBitmap(html: String, pageWidthPx: Int): Bitmap =
//        suspendCancellableCoroutine { cont ->
//            val webView = WebView(context)
//            webView.settings.javaScriptEnabled = true
//            // Software layer gives much more reliable draw() output than hardware layers,
//            // especially for a WebView that's never attached to a window.
//            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
//
//            webView.webViewClient = object : WebViewClient() {
//                override fun onPageFinished(view: WebView, url: String) {
//                    // Small delay lets images/fonts finish laying out before we measure.
//                    view.postDelayed({
//                        view.evaluateJavascript("document.documentElement.scrollHeight.toString()") { raw ->
//                            val density = view.resources.displayMetrics.density
//                            val cssHeight = raw.trim('"').toIntOrNull() ?: 1123 // fallback ~A4 at 96dpi
//                            val contentHeightPx = (cssHeight * density).toInt().coerceAtLeast(1)
//
//                            val widthSpec = View.MeasureSpec.makeMeasureSpec(pageWidthPx, View.MeasureSpec.EXACTLY)
//                            val heightSpec = View.MeasureSpec.makeMeasureSpec(contentHeightPx, View.MeasureSpec.EXACTLY)
//                            view.measure(widthSpec, heightSpec)
//                            view.layout(0, 0, pageWidthPx, contentHeightPx)
//
//                            val bmp = Bitmap.createBitmap(pageWidthPx, contentHeightPx, Bitmap.Config.ARGB_8888)
//                            val canvas = Canvas(bmp)
//                            canvas.drawColor(Color.WHITE)
//                            view.draw(canvas)
//
//                            cont.resume(bmp)
//                        }
//                    }, 300)
//                }
//            }
//            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
//        }
//
//    private fun writeBitmapToPdf(bitmap: Bitmap, outputFileName: String): String {
//        val exportDir = File(context.filesDir, "exports").apply { mkdirs() }
//        val outputFile = File(exportDir, outputFileName)
//
//        val pageWidth = bitmap.width
//        val pageHeightPerPage = 842 // ~A4 height at same scale as 595 width
//
//        val pdfDocument = PdfDocument()
//        try {
//            var y = 0
//            var pageNum = 1
//            while (y < bitmap.height) {
//                val sliceHeight = minOf(pageHeightPerPage, bitmap.height - y)
//                val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, sliceHeight, pageNum).create()
//                val page = pdfDocument.startPage(pageInfo)
//
//                val srcRect = Rect(0, y, pageWidth, y + sliceHeight)
//                val destRect = Rect(0, 0, pageWidth, sliceHeight)
//                page.canvas.drawBitmap(bitmap, srcRect, destRect, null)
//
//                pdfDocument.finishPage(page)
//                y += sliceHeight
//                pageNum++
//            }
//            FileOutputStream(outputFile).use { pdfDocument.writeTo(it) }
//        } finally {
//            pdfDocument.close()
//        }
//        return outputFile.absolutePath
//    }
//}
//// shared/src/androidMain/kotlin/com/example/luxeval/presentation/report/PdfExporter.android.kt
//package com.example.luxeval.presentation.report
//
//import android.content.Context
//import android.os.ParcelFileDescriptor
//import android.print.PageRange
//import android.print.PrintAttributes
//import android.print.PrintDocumentAdapter
//import android.webkit.WebView
//import android.webkit.WebViewClient
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.suspendCancellableCoroutine
//import kotlinx.coroutines.withContext
//import java.io.File
//import kotlin.coroutines.resume
//import kotlin.coroutines.resumeWithException
//
//actual class PdfExporter(private val context: Context) {
//
//    actual suspend fun exportToPdf(html: String, outputFileName: String): String =
//        withContext(Dispatchers.Main) {   // WebView must be touched on the main thread
//            val outputFile = File(context.filesDir, "exports/$outputFileName").apply {
//                parentFile?.mkdirs()
//            }
//
//            val webView = WebView(context)
//            webView.settings.javaScriptEnabled = false
//
//            // Step 1: wait for the HTML to finish loading
//            suspendCancellableCoroutine<Unit> { cont ->
//                webView.webViewClient = object : WebViewClient() {
//                    override fun onPageFinished(view: WebView, url: String) {
//                        if (cont.isActive) cont.resume(Unit)
//                    }
//                }
//                webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
//            }
//
//            // Step 2: drive the print adapter manually, writing bytes to our own file
//            // instead of the system print dialog
//            val adapter = webView.createPrintDocumentAdapter(outputFileName)
//            val attributes = PrintAttributes.Builder()
//                .setMediaSize(PrintAttributes.MediaSize.NA_LETTER)
//                .setResolution(PrintAttributes.Resolution("pdf", "pdf", 300, 300))
//                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
//                .build()
//
//            suspendCancellableCoroutine<Unit> { cont ->
//                adapter.onLayout(
//                    null, attributes, null,
//                    object : PrintDocumentAdapter.LayoutResultCallback() {
//                        override fun onLayoutFinished(info: android.print.PrintDocumentInfo?, changed: Boolean) {
//                            val pfd = ParcelFileDescriptor.open(
//                                outputFile,
//                                ParcelFileDescriptor.MODE_CREATE or
//                                        ParcelFileDescriptor.MODE_TRUNCATE or
//                                        ParcelFileDescriptor.MODE_READ_WRITE
//                            )
//                            adapter.onWrite(
//                                arrayOf(PageRange.ALL_PAGES), pfd, null,
//                                object : PrintDocumentAdapter.WriteResultCallback() {
//                                    override fun onWriteFinished(pages: Array<out PageRange>?) {
//                                        pfd.close()
//                                        if (cont.isActive) cont.resume(Unit)
//                                    }
//                                    override fun onWriteFailed(error: CharSequence?) {
//                                        pfd.close()
//                                        if (cont.isActive) cont.resumeWithException(Exception(error?.toString() ?: "PDF write failed"))
//                                    }
//                                }
//                            )
//                        }
//                        override fun onLayoutFailed(error: CharSequence?) {
//                            if (cont.isActive) cont.resumeWithException(Exception(error?.toString() ?: "PDF layout failed"))
//                        }
//                    },
//                    null
//                )
//            }
//
//            outputFile.absolutePath
//        }
//}