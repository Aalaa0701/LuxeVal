package com.example.luxeval.presentation.report

expect class PdfExporter {
    /** Renders `html` to a PDF and saves it under the app's exports directory.
     *  Returns the absolute file path on success. */
    suspend fun exportToPdf(html: String, outputFileName: String): String
}