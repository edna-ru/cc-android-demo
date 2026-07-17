package edna.chatcenter.demo.appCode.business

import android.os.Build

/**
 * Режим edge-to-edge. Определяет минимальную версию Android, начиная с которой приложение переводит
 * окна в режим edge-to-edge и обрабатывает системные отступы. Значение используется в трёх местах:
 * вызов `enableEdgeToEdge()` в активити, обработка отступов встроенного чата и конфигурация SDK
 * (`ChatFlow.edgeToEdge`, `ChatFlow.hostInsets`, `GalleryFlow.edgeToEdge`).
 *
 * Промежуточный порог один ([FROM_ANDROID_11]): поведение системных отступов и клавиатуры на
 * Android 11–14 совпадает, а отличия появляются между Android 10 и 11 (анимация отступов, отдельный
 * инсет клавиатуры).
 *
 * [LEGACY] соответствует порогу Android 15 (API 35): на Android 15 и выше при `targetSdk >= 35`
 * режим edge-to-edge включается платформой и не может быть отключён, на более ранних версиях окно
 * остаётся обычным.
 *
 * @param enableFromSdk минимальный уровень API, с которого режим активен. [ALL_VERSIONS] использует
 *   минимальный поддерживаемый уровень.
 */
enum class EdgeToEdgeMode(val enableFromSdk: Int) {
    LEGACY(Build.VERSION_CODES.VANILLA_ICE_CREAM),
    FROM_ANDROID_11(Build.VERSION_CODES.R),
    ALL_VERSIONS(Build.VERSION_CODES.LOLLIPOP);

    /** Активен ли режим на данной версии Android. */
    fun isActiveOn(sdkInt: Int = Build.VERSION.SDK_INT): Boolean = sdkInt >= enableFromSdk

    companion object {
        val DEFAULT = LEGACY

        /** Восстанавливает режим по сохранённому порогу [enableFromSdk];
         * неизвестное — [DEFAULT]. */
        fun fromStored(threshold: Int): EdgeToEdgeMode {
            return entries.firstOrNull { it.enableFromSdk == threshold } ?: DEFAULT
        }
    }
}
