package edna.chatcenter.demo.appCode.business

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * Хранилище настроек демо (SharedPreferences `sdk_settings`). Инкапсулирует все
 * ключи, значения по умолчанию, чтение и запись пользовательских настроек приложения.
 *
 * Регистрируется в DI как единственный экземпляр на всё приложение (см. `appModule`).
 */
class SettingsStorage(context: Context) {

    private val preferences: SharedPreferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )

    var asyncInitEnabled: Boolean
        get() = preferences.getBoolean(KEY_ASYNC_INIT, true)
        set(value) = preferences.edit { putBoolean(KEY_ASYNC_INIT, value) }

    var asyncAuthEnabled: Boolean
        get() = preferences.getBoolean(KEY_ASYNC_AUTH, true)
        set(value) = preferences.edit { putBoolean(KEY_ASYNC_AUTH, value) }

    var searchEnabled: Boolean
        get() = preferences.getBoolean(KEY_SEARCH, true)
        set(value) = preferences.edit { putBoolean(KEY_SEARCH, value) }

    var voiceMessagesEnabled: Boolean
        get() = preferences.getBoolean(KEY_VOICE_MESSAGES, true)
        set(value) = preferences.edit { putBoolean(KEY_VOICE_MESSAGES, value) }

    var openGraphEnabled: Boolean
        get() = preferences.getBoolean(KEY_OPEN_GRAPH, true)
        set(value) = preferences.edit { putBoolean(KEY_OPEN_GRAPH, value) }

    var keepWebSocketEnabled: Boolean
        get() = preferences.getBoolean(KEY_KEEP_WEB_SOCKET, false)
        set(value) = preferences.edit { putBoolean(KEY_KEEP_WEB_SOCKET, value) }

    var keepWebSocketDuringSessionEnabled: Boolean
        get() = preferences.getBoolean(KEY_KEEP_WEB_SOCKET_DURING_SESSION, false)
        set(value) = preferences.edit { putBoolean(KEY_KEEP_WEB_SOCKET_DURING_SESSION, value) }

    var webSocketReconnectEnabled: Boolean
        get() = preferences.getBoolean(KEY_WEB_SOCKET_RECONNECT, false)
        set(value) = preferences.edit { putBoolean(KEY_WEB_SOCKET_RECONNECT, value) }

    var sdkInitDelayEnabled: Boolean
        get() = preferences.getBoolean(KEY_SDK_INIT_DELAY, false)
        set(value) = preferences.edit { putBoolean(KEY_SDK_INIT_DELAY, value) }

    var subtitleShowOrgUnitEnabled: Boolean
        get() = preferences.getBoolean(KEY_SUBTITLE_SHOW_ORG_UNIT, true)
        set(value) = preferences.edit { putBoolean(KEY_SUBTITLE_SHOW_ORG_UNIT, value) }

    var showSendMenuOnShakeEnabled: Boolean
        get() = preferences.getBoolean(KEY_SHOW_SEND_MENU_ON_SHAKE, true)
        set(value) = preferences.edit { putBoolean(KEY_SHOW_SEND_MENU_ON_SHAKE, value) }

    var edgeToEdgeMode: EdgeToEdgeMode
        get() = EdgeToEdgeMode.fromStored(
            preferences.getInt(KEY_EDGE_TO_EDGE_MODE, EdgeToEdgeMode.DEFAULT.enableFromSdk)
        )
        set(value) = preferences.edit { putInt(KEY_EDGE_TO_EDGE_MODE, value.enableFromSdk) }

    /**
     * Сохранённый режим темы приложения (значения [androidx.appcompat.app.AppCompatDelegate]).
     * Значение по умолчанию задаётся вызывающим кодом, так как зависит от версии Android.
     */
    fun appDayNightTheme(default: Int): Int = preferences.getInt(KEY_APP_DAY_NIGHT_THEME, default)

    fun setAppDayNightTheme(mode: Int) = preferences.edit { putInt(KEY_APP_DAY_NIGHT_THEME, mode) }

    companion object {
        private const val PREFERENCES_NAME = "sdk_settings"

        private const val KEY_ASYNC_INIT = "sdk_async_init_enabled"
        private const val KEY_ASYNC_AUTH = "sdk_async_authorization_enabled"
        private const val KEY_SEARCH = "sdk_search_enabled"
        private const val KEY_VOICE_MESSAGES = "sdk_voice_messages_enabled"
        private const val KEY_OPEN_GRAPH = "sdk_opengraph_enabled"
        private const val KEY_KEEP_WEB_SOCKET = "sdk_keep_websocket_enabled"
        private const val KEY_KEEP_WEB_SOCKET_DURING_SESSION =
            "sdk_keep_websocket_enabled_during_session"
        private const val KEY_WEB_SOCKET_RECONNECT = "sdk_websocket_reconnect_enabled"
        private const val KEY_SDK_INIT_DELAY = "sdk_init_delay_enabled"
        private const val KEY_SUBTITLE_SHOW_ORG_UNIT = "sdk_subtitle_show_org_unit_enabled"
        private const val KEY_SHOW_SEND_MENU_ON_SHAKE = "sdk_show_send_menu_on_shake_enabled"
        private const val KEY_APP_DAY_NIGHT_THEME = "sdk_app_day_night_theme"
        private const val KEY_EDGE_TO_EDGE_MODE = "sdk_edge_to_edge_mode"
    }
}
