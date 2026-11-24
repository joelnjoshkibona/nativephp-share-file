package com.blutrixx.plugins.sharefile

import android.content.Intent
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.nativephp.mobile.bridge.BridgeFunction
import java.io.File as JFile

/**
 * Android's own share list for a file the app hands over (a receipt's PDF, say). The file is copied into the
 * app's cache folder, the one the app's FileProvider already serves, and shared from there as a read-only link: the
 * other app never gets the app's own files.
 */
object ShareFunctions {

    class ShareFile(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            val path = parameters["filePath"] as? String ?: return mapOf("error" to "failed")
            val title = (parameters["title"] as? String).orEmpty()
            val message = (parameters["message"] as? String).orEmpty()
            val source = JFile(path)
            if (!source.isFile) return mapOf("error" to "failed")

            return try {
                val dir = JFile(activity.cacheDir, "shared").also { it.mkdirs() }
                val copy = JFile(dir, source.name.replace(Regex("[^A-Za-z0-9._-]"), "_"))
                source.copyTo(copy, overwrite = true)
                val uri = FileProvider.getUriForFile(activity, "${activity.packageName}.fileprovider", copy)
                val type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(copy.extension.lowercase()) ?: "application/octet-stream"
                val send = Intent(Intent.ACTION_SEND).apply {
                    this.type = type
                    putExtra(Intent.EXTRA_STREAM, uri)
                    if (title.isNotEmpty()) putExtra(Intent.EXTRA_SUBJECT, title)
                    if (message.isNotEmpty()) putExtra(Intent.EXTRA_TEXT, message)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                activity.runOnUiThread { activity.startActivity(Intent.createChooser(send, title.ifEmpty { null })) }
                mapOf("shared" to true)
            } catch (e: Exception) {
                mapOf("error" to "failed")
            }
        }
    }

    class ShareUrl(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            val url = parameters["url"] as? String ?: return mapOf("error" to "failed")
            val title = (parameters["title"] as? String).orEmpty()
            val text = (parameters["text"] as? String).orEmpty()
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                if (title.isNotEmpty()) putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, if (text.isEmpty()) url else "$text $url")
            }
            activity.runOnUiThread { activity.startActivity(Intent.createChooser(send, title.ifEmpty { null })) }
            return mapOf("shared" to true)
        }
    }
}
