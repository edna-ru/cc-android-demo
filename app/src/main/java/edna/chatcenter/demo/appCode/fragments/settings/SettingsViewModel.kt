package edna.chatcenter.demo.appCode.fragments.settings

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences: SharedPreferences = getApplication<Application>()
        .getSharedPreferences(settingsPreferencesName, Context.MODE_PRIVATE)

    private val _searchEnabled = MutableLiveData(sharedPreferences.getBoolean(settingsKeySearch, true))
    val searchEnabled: LiveData<Boolean> = _searchEnabled

    private val _voiceMessagesEnabled = MutableLiveData(sharedPreferences.getBoolean(settingsKeyVoiceMessages, true))
    val voiceMessagesEnabled: LiveData<Boolean> = _voiceMessagesEnabled

    private val _openGraphEnabled = MutableLiveData(sharedPreferences.getBoolean(settingsKeyOpenGraph, true))
    val openGraphEnabled: LiveData<Boolean> = _openGraphEnabled

    private val _keepWebSocketEnabled = MutableLiveData(sharedPreferences.getBoolean(settingsKeyKeepWebSocket, false))
    val keepWebSocketEnabled: LiveData<Boolean> = _keepWebSocketEnabled

    fun setSearchEnabled(enabled: Boolean) {
        _searchEnabled.value = enabled
        sharedPreferences.edit().putBoolean(settingsKeySearch, enabled).apply()
    }

    fun setVoiceMessagesEnabled(enabled: Boolean) {
        _voiceMessagesEnabled.value = enabled
        sharedPreferences.edit().putBoolean(settingsKeyVoiceMessages, enabled).apply()
    }

    fun setOpenGraphEnabled(enabled: Boolean) {
        _openGraphEnabled.value = enabled
        sharedPreferences.edit().putBoolean(settingsKeyOpenGraph, enabled).apply()
    }

    fun setKeepWebSocketEnabled(enabled: Boolean) {
        _keepWebSocketEnabled.value = enabled
        sharedPreferences.edit().putBoolean(settingsKeyKeepWebSocket, enabled).apply()
    }
}

const val settingsPreferencesName: String = "sdk_settings"
const val settingsKeySearch: String = "sdk_search_enabled"
const val settingsKeyVoiceMessages: String = "sdk_voice_messages_enabled"
const val settingsKeyOpenGraph: String = "sdk_opengraph_enabled"
const val settingsKeyKeepWebSocket: String = "sdk_keep_websocket_enabled"
