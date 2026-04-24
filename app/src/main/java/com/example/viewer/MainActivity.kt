package com.example.viewer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var pdfImage: ImageView
    private lateinit var statusText: TextView
    private var defaultDialog: AlertDialog? = null

    private var pdfFileDescriptor: ParcelFileDescriptor? = null
    private var pdfRenderer: PdfRenderer? = null
    private var currentPage: PdfRenderer.Page? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pdfImage = findViewById(R.id.pdfImage)
        statusText = findViewById(R.id.statusText)

        handlePdfIntent(intent)
    }

    override fun onResume() {
        super.onResume()
        maybeHandleDefaultFlow()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handlePdfIntent(intent)
    }

    override fun onDestroy() {
        defaultDialog?.dismiss()
        defaultDialog = null
        closePdfResources()
        super.onDestroy()
    }

    private fun maybeHandleDefaultFlow() {
        val openedFromPdfIntent = intent?.action == Intent.ACTION_VIEW && intent?.data != null
        if (openedFromPdfIntent) {
            defaultDialog?.dismiss()
            defaultDialog = null
            return
        }

        statusText.text = getString(R.string.default_pdf_not_verified)

        if (defaultDialog?.isShowing == true) {
            return
        }

        defaultDialog = AlertDialog.Builder(this)
            .setTitle(R.string.default_pdf_title)
            .setMessage(R.string.default_pdf_message)
            .setCancelable(false)
            .setPositiveButton(R.string.default_pdf_set_now) { _, _ ->
                openDefaultAppSettings()
            }
            .setNeutralButton(R.string.default_pdf_i_set) { _, _ ->
                statusText.text = getString(R.string.default_pdf_verified)
                Toast.makeText(this, getString(R.string.default_pdf_verified), Toast.LENGTH_SHORT)
                    .show()
            }
            .setNegativeButton(R.string.default_pdf_exit) { _, _ ->
                finish()
            }
            .show()
    }

    private fun openDefaultAppSettings() {
        val candidates = buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Intent(Settings.ACTION_APP_OPEN_BY_DEFAULT_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                })
            }

            add(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
            })
        }

        val launchIntent = candidates.firstOrNull { intent ->
            intent.resolveActivity(packageManager) != null
        }

        if (launchIntent != null) {
            startActivity(launchIntent)
        } else {
            Toast.makeText(this, getString(R.string.default_pdf_settings_unavailable), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun handlePdfIntent(intent: Intent?) {
        val uri = intent?.data

        if (intent?.action != Intent.ACTION_VIEW || uri == null) {
            statusText.text = getString(R.string.no_pdf_opened)
            return
        }

        openPdf(uri)
    }

    private fun openPdf(uri: Uri) {
        closePdfResources()

        runCatching {
            val descriptor = contentResolver.openFileDescriptor(uri, "r")
                ?: throw IllegalStateException("Readable PDF file descriptor not available")

            pdfFileDescriptor = descriptor
            pdfRenderer = PdfRenderer(descriptor)

            if (pdfRenderer?.pageCount == 0) {
                throw IllegalStateException("PDF has no pages")
            }

            val page = pdfRenderer!!.openPage(0)
            currentPage = page

            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            bitmap.eraseColor(Color.WHITE)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            pdfImage.setImageBitmap(bitmap)
            statusText.text = getString(R.string.pdf_opened, uri.lastPathSegment ?: uri.toString())
        }.onFailure { error ->
            pdfImage.setImageDrawable(null)
            statusText.text = getString(
                R.string.pdf_open_failed,
                error.localizedMessage ?: "Unknown error"
            )
        }
    }

    private fun closePdfResources() {
        currentPage?.close()
        currentPage = null

        pdfRenderer?.close()
        pdfRenderer = null

        pdfFileDescriptor?.close()
        pdfFileDescriptor = null
    }
}