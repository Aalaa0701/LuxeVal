package com.example.luxeval.presentation.report

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreGraphics.CGRectMake
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSMutableData
import platform.Foundation.NSUserDomainMask
import platform.Foundation.setValue
import platform.Foundation.writeToFile
import platform.UIKit.NSStringDrawingContext
import platform.UIKit.UIGraphicsBeginPDFContextToData
import platform.UIKit.UIGraphicsBeginPDFPage
import platform.UIKit.UIGraphicsEndPDFContext
import platform.UIKit.UIPrintPageRenderer
import platform.UIKit.viewPrintFormatter
import platform.WebKit.WKNavigation
import platform.WebKit.WKNavigationDelegateProtocol
import platform.WebKit.WKWebView
import platform.darwin.NSObject
import kotlin.coroutines.resume

@OptIn(ExperimentalForeignApi::class)
private class LoadWaiter(private val onLoaded: () -> Unit) : NSObject(), WKNavigationDelegateProtocol {
    override fun webView(webView: WKWebView, didFinishNavigation: WKNavigation?) {
        onLoaded()
    }
}

@OptIn(ExperimentalForeignApi::class)
actual class PdfExporter {

    actual suspend fun exportToPdf(html: String, outputFileName: String): String {
        val webView = WKWebView(frame = CGRectMake(0.0, 0.0, 612.0, 792.0)) // US Letter points

        suspendCancellableCoroutine<Unit> { cont ->
            val delegate = LoadWaiter { cont.resume(Unit) }
            webView.navigationDelegate = delegate
            webView.loadHTMLString(html, baseURL = null)
        }

        val renderer = UIPrintPageRenderer()
        renderer.addPrintFormatter(webView.viewPrintFormatter(), startingAtPageAtIndex = 0)
        val pageRect = CGRectMake(0.0, 0.0, 612.0, 792.0)
        renderer.setValue(pageRect, forKey = "paperRect")
        renderer.setValue(pageRect, forKey = "printableRect")

        val pdfData = NSMutableData()
        UIGraphicsBeginPDFContextToData(pdfData, pageRect, null)
        val pageCount = renderer.numberOfPages()
        for (i in 0 until pageCount.toInt()) {
            UIGraphicsBeginPDFPage()
            renderer.drawPageAtIndex(i.toLong(), inRect = pageRect)
        }
        UIGraphicsEndPDFContext()

        val docsDir = requireNotNull(
            NSFileManager.defaultManager
                .URLsForDirectory(NSDocumentDirectory, NSUserDomainMask)
                .firstOrNull() as? platform.Foundation.NSURL
        ).path!!
        val exportsDir = "$docsDir/exports"
        NSFileManager.defaultManager.createDirectoryAtPath(exportsDir, true, null, null)
        val path = "$exportsDir/$outputFileName"
        pdfData.writeToFile(path, true)
        return path
    }
}