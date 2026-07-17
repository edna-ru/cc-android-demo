package edna.chatcenter.demo.appCode.fragments.settings

import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import edna.chatcenter.demo.appCode.business.EdgeToEdgeMode
import edna.chatcenter.demo.appCode.business.SettingsStorage

class SettingsViewModel(private val settings: SettingsStorage) : ViewModel() {

    private val _asyncInitEnabled = MutableLiveData(settings.asyncInitEnabled)
    val asyncInitEnabled: LiveData<Boolean> = _asyncInitEnabled

    private val _asyncAuthorizationEnabled = MutableLiveData(settings.asyncAuthEnabled)
    val asyncAuthorizationEnabled: LiveData<Boolean> = _asyncAuthorizationEnabled

    private val _searchEnabled = MutableLiveData(settings.searchEnabled)
    val searchEnabled: LiveData<Boolean> = _searchEnabled

    private val _voiceMessagesEnabled = MutableLiveData(settings.voiceMessagesEnabled)
    val voiceMessagesEnabled: LiveData<Boolean> = _voiceMessagesEnabled

    private val _openGraphEnabled = MutableLiveData(settings.openGraphEnabled)
    val openGraphEnabled: LiveData<Boolean> = _openGraphEnabled

    private val _keepWebSocketEnabled = MutableLiveData(settings.keepWebSocketEnabled)
    val keepWebSocketEnabled: LiveData<Boolean> = _keepWebSocketEnabled

    private val _keepWebSocketEnabledDuringSession =
        MutableLiveData(settings.keepWebSocketDuringSessionEnabled)
    val keepWebSocketEnabledDuringSession: LiveData<Boolean> = _keepWebSocketEnabledDuringSession

    private val _webSocketReconnectEnabled = MutableLiveData(settings.webSocketReconnectEnabled)
    val webSocketReconnectEnabled: LiveData<Boolean> = _webSocketReconnectEnabled

    private val _isSdkInitDelayEnabled = MutableLiveData(settings.sdkInitDelayEnabled)
    val isSdkInitDelayEnabled: LiveData<Boolean> = _isSdkInitDelayEnabled

    private val _isSubtitleShowOrgUnitEnabled = MutableLiveData(settings.subtitleShowOrgUnitEnabled)
    val isSubtitleShowOrgUnitEnabled: LiveData<Boolean> = _isSubtitleShowOrgUnitEnabled

    private val _appDayNightTheme =
        MutableLiveData(settings.appDayNightTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM))
    val appDayNightMode: LiveData<Int> = _appDayNightTheme

    private val _edgeToEdgeMode = MutableLiveData(settings.edgeToEdgeMode)
    val edgeToEdgeMode: LiveData<EdgeToEdgeMode> = _edgeToEdgeMode

    fun setAsyncInitEnabled(enabled: Boolean) {
        _asyncInitEnabled.value = enabled
        settings.asyncInitEnabled = enabled
    }

    fun setAsyncAuthorizationEnabled(enabled: Boolean) {
        _asyncAuthorizationEnabled.value = enabled
        settings.asyncAuthEnabled = enabled
    }

    fun setSearchEnabled(enabled: Boolean) {
        _searchEnabled.value = enabled
        settings.searchEnabled = enabled
    }

    fun setVoiceMessagesEnabled(enabled: Boolean) {
        _voiceMessagesEnabled.value = enabled
        settings.voiceMessagesEnabled = enabled
    }

    fun setOpenGraphEnabled(enabled: Boolean) {
        _openGraphEnabled.value = enabled
        settings.openGraphEnabled = enabled
    }

    fun setKeepWebSocketEnabled(enabled: Boolean) {
        _keepWebSocketEnabled.value = enabled
        settings.keepWebSocketEnabled = enabled
    }

    fun setKeepWebSocketEnabledDuringSession(enabled: Boolean) {
        _keepWebSocketEnabledDuringSession.value = enabled
        settings.keepWebSocketDuringSessionEnabled = enabled
    }

    fun setWebSocketReconnectEnabled(enabled: Boolean) {
        _webSocketReconnectEnabled.value = enabled
        settings.webSocketReconnectEnabled = enabled
    }

    fun setSdkInitDelayEnabled(enabled: Boolean) {
        _isSdkInitDelayEnabled.value = enabled
        settings.sdkInitDelayEnabled = enabled
    }

    fun setSubtitleShowOrgUnitEnabled(enabled: Boolean) {
        _isSubtitleShowOrgUnitEnabled.value = enabled
        settings.subtitleShowOrgUnitEnabled = enabled
    }

    fun setAppDayNightTheme(mode: Int) {
        _appDayNightTheme.value = mode
        settings.setAppDayNightTheme(mode)
    }

    fun setEdgeToEdgeMode(mode: EdgeToEdgeMode) {
        _edgeToEdgeMode.value = mode
        settings.edgeToEdgeMode = mode
    }
}
