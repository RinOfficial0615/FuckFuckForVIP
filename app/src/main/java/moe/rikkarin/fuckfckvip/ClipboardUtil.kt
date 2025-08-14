package moe.rikkarin.fuckfckvip

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

object ClipboardUtil {
    fun getText(context: Context): String? {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null && clip.itemCount > 0) {
            return clip.getItemAt(0).coerceToText(context).toString().takeIf(String::isNotBlank)
        }
        return null
    }

    fun copyText(context: Context, text: String) {
        if (text.isBlank())
            return

        val clipboard: ClipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("text", text))
    }

    fun String.copyToClipboard(context: Context) {
        copyText(context, this)
    }
}