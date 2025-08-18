package moe.rikkarin.fuckfckvip

import android.os.Build
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object Hasher {
    private const val HASH_SALT = "xwqdTjFWjSUsxYKWALKY3Et3/eSmHVnI3PrQUyEuxzA="

    private fun hashString(s: String): String {
        try {
            val digest =
                MessageDigest.getInstance("SHA-256").digest(s.toByteArray(StandardCharsets.UTF_8))
            val stringBuilder0 = StringBuilder()
            for (v in digest.indices) {
                val s1 = Integer.toHexString(digest[v].toInt() and 0xFF)
                if (s1.length == 1) {
                    stringBuilder0.append('0')
                }

                stringBuilder0.append(s1)
            }

            return stringBuilder0.toString()
        } catch (_: Exception) {
            return ""
        }
    }

    fun getCurrentDeviceHash() =
        hashString(Build.BOARD + Build.BRAND + Build.DEVICE + Build.HARDWARE + Build.MANUFACTURER + Build.MODEL + Build.PRODUCT)

    fun getSignature(text: String? = null): String {
        if (text?.isBlank() == true)
            return ""
        return hashString((text ?: getCurrentDeviceHash()) + HASH_SALT)
    }

}