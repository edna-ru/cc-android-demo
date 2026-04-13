package edna.chatcenter.demo.appCode.fragments.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences: SharedPreferences = getApplication<Application>()
        .getSharedPreferences(settingsPreferencesName, Context.MODE_PRIVATE)

    private val _asyncInitEnabled = MutableLiveData(sharedPreferences.getBoolean(settingsKeyAsyncInit, true))
    val asyncInitEnabled: LiveData<Boolean> = _asyncInitEnabled

    private val _searchEnabled = MutableLiveData(sharedPreferences.getBoolean(settingsKeySearch, true))
    val searchEnabled: LiveData<Boolean> = _searchEnabled

    private val _voiceMessagesEnabled = MutableLiveData(sharedPreferences.getBoolean(settingsKeyVoiceMessages, true))
    val voiceMessagesEnabled: LiveData<Boolean> = _voiceMessagesEnabled

    private val _openGraphEnabled = MutableLiveData(sharedPreferences.getBoolean(settingsKeyOpenGraph, true))
    val openGraphEnabled: LiveData<Boolean> = _openGraphEnabled

    private val _keepWebSocketEnabled = MutableLiveData(sharedPreferences.getBoolean(settingsKeyKeepWebSocket, false))
    val keepWebSocketEnabled: LiveData<Boolean> = _keepWebSocketEnabled

    private val _keepWebSocketEnabledDuringSession = MutableLiveData(sharedPreferences.getBoolean(settingsKeyKeepWebSocketDuringSession, false))
    val keepWebSocketEnabledDuringSession: LiveData<Boolean> = _keepWebSocketEnabledDuringSession

    private val _appDayNightTheme = MutableLiveData(sharedPreferences.getInt(settingsKeyAppDayNightTheme, -1))
    val appDayNightMode: LiveData<Int> = _appDayNightTheme

    fun setAsyncInitEnabled(enabled: Boolean) {
        _asyncInitEnabled.value = enabled
        sharedPreferences.edit { putBoolean(settingsKeyAsyncInit, enabled) }
    }

    fun setSearchEnabled(enabled: Boolean) {
        _searchEnabled.value = enabled
        sharedPreferences.edit { putBoolean(settingsKeySearch, enabled) }
    }

    fun setVoiceMessagesEnabled(enabled: Boolean) {
        _voiceMessagesEnabled.value = enabled
        sharedPreferences.edit { putBoolean(settingsKeyVoiceMessages, enabled) }
    }

    fun setOpenGraphEnabled(enabled: Boolean) {
        _openGraphEnabled.value = enabled
        sharedPreferences.edit { putBoolean(settingsKeyOpenGraph, enabled) }
    }

    fun setKeepWebSocketEnabled(enabled: Boolean) {
        _keepWebSocketEnabled.value = enabled
        sharedPreferences.edit { putBoolean(settingsKeyKeepWebSocket, enabled) }
    }

    fun setKeepWebSocketEnabledDuringSession(enabled: Boolean) {
        _keepWebSocketEnabledDuringSession.value = enabled
        sharedPreferences.edit { putBoolean(settingsKeyKeepWebSocketDuringSession, enabled) }
    }

    fun setAppDayNightTheme(mode: Int) {
        _appDayNightTheme.value = mode
        sharedPreferences.edit { putInt(settingsKeyAppDayNightTheme, mode) }
    }
}

const val settingsPreferencesName: String = "sdk_settings"
const val settingsKeyAsyncInit: String = "sdk_async_init_enabled"
const val settingsKeySearch: String = "sdk_search_enabled"
const val settingsKeyVoiceMessages: String = "sdk_voice_messages_enabled"
const val settingsKeyOpenGraph: String = "sdk_opengraph_enabled"
const val settingsKeyKeepWebSocket: String = "sdk_keep_websocket_enabled"
const val settingsKeyKeepWebSocketDuringSession: String = "sdk_keep_websocket_enabled_during_session"
const val settingsKeyAppDayNightTheme: String = "sdk_app_day_night_theme"
