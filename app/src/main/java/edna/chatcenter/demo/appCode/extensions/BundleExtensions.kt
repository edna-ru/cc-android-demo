package edna.chatcenter.demo.appCode.extensions

import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.BundleCompat

/**
 * Достаёт [Parcelable] по ключу через [BundleCompat]
 *
 * Типизированный `Bundle.getParcelable` на Android 13 бросает `NullPointerException`,
 * поэтому [BundleCompat] вызывает его только с API 34
 */
inline fun <reified T : Parcelable> Bundle.getCompatParcelable(key: String): T? {
    return BundleCompat.getParcelable(this, key, T::class.java)
}