package edna.chatcenter.demo.integrationCode

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessaging
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import edna.chatcenter.core.config.transport.ChatNetworkConfig
import edna.chatcenter.core.config.transport.ChatSSLCertificate
import edna.chatcenter.core.config.transport.ChatTransportConfig
import edna.chatcenter.core.config.transport.HTTPConfig
import edna.chatcenter.core.config.transport.SSLPinningConfig
import edna.chatcenter.core.config.transport.WSConfig
import edna.chatcenter.core.logger.ChatLogLevel
import edna.chatcenter.core.logger.ChatLoggerConfig
import edna.chatcenter.core.main.ChatCenterUIListener
import edna.chatcenter.core.models.enums.ChatApiVersion
import edna.chatcenter.demo.R
import edna.chatcenter.demo.appCode.business.PreferencesProvider
import edna.chatcenter.demo.appCode.business.ServersProvider
import edna.chatcenter.demo.appCode.business.appModule
import edna.chatcenter.demo.appCode.fragments.log.LogViewModel
import edna.chatcenter.demo.appCode.fragments.settings.settingsKeyKeepWebSocket
import edna.chatcenter.demo.appCode.fragments.settings.settingsKeyKeepWebSocketDuringSession
import edna.chatcenter.demo.appCode.fragments.settings.settingsKeyOpenGraph
import edna.chatcenter.demo.appCode.fragments.settings.settingsKeySearch
import edna.chatcenter.demo.appCode.fragments.settings.settingsKeyVoiceMessages
import edna.chatcenter.demo.appCode.fragments.settings.settingsPreferencesName
import edna.chatcenter.demo.appCode.models.LogModel
import edna.chatcenter.demo.appCode.models.ServerConfig
import edna.chatcenter.demo.appCode.push.HCMTokenRefresher
import edna.chatcenter.demo.integrationCode.fragments.launch.LaunchFragment
import edna.chatcenter.ui.visual.ChatConfig
import edna.chatcenter.ui.visual.core.ChatCenterUI
import edna.chatcenter.ui.visual.uiStyle.settings.ChatTheme
import edna.chatcenter.ui.visual.uiStyle.settings.components.ChatComponents
import edna.chatcenter.ui.visual.uiStyle.settings.flows.ChatFlows
import edna.chatcenter.ui.visual.uiStyle.settings.theme.ChatColors
import edna.chatcenter.ui.visual.uiStyle.settings.theme.ChatImages
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EdnaChatCenterApplication : Application() {
    private var chatLightTheme: ChatTheme? = null
    private var chatDarkTheme: ChatTheme? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val asyncInit = false

    private val serversProvider: ServersProvider by inject()
    private val preferences: PreferencesProvider by inject()

    var chatCenterUI: ChatCenterUI? = null

    val unreadCountMessagesFlow = MutableStateFlow(0U)

    override fun onCreate() {
        super.onCreate()
        startAppCenter()

        startKoin {
            androidContext(this@EdnaChatCenterApplication)
            modules(appModule)
        }

        preferences.resetDemoMode()
        initLibrary()
    }

    private fun initLibrary() {
        if (asyncInit) {
            coroutineScope.launch {
                initThemes()
                initChatCenterUI()
                val intent = Intent(LaunchFragment.APP_INIT_THREADS_LIB_ACTION)
                    .setClass(
                        this@EdnaChatCenterApplication,
                        LaunchFragment.InitThreadsLibReceiver::class.java
                    )
                applicationContext.sendBroadcast(intent)
            }
        } else {
            initThemes()
            initChatCenterUI()
        }
        checkAndUpdateTokens()
    }

    fun subscribeToUnreadMessages() {
        chatCenterUI?.setChatCenterUIListener(object : ChatCenterUIListener {
            override fun unreadMessageCountChanged(count: UInt) {
                Log.i("ELog", "New unread messages count: $count")
                CoroutineScope(Dispatchers.Main).launch { unreadCountMessagesFlow.emit(count) }
            }

            override fun urlClicked(url: String) {
                super.urlClicked(url)
                Log.i("UrlClicked", url)
            }
        })
    }

    private fun checkAndUpdateTokens() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                ChatCenterUI.setFCMToken(token, applicationContext)
            }
        }
        coroutineScope.launch {
            HCMTokenRefresher.requestToken(this@EdnaChatCenterApplication)
        }
    }

    private fun initThemes() {
        // Пример задания тем
        val lightColors = ChatColors(
            main = R.color.light_main,
            searchingProgressLoader = R.color.light_main,
            bodyIconsTint = R.color.light_main,
            incomingText = R.color.black_color,
            incomingTimeText = R.color.light_time_text,
            outgoingTimeText = R.color.light_time_text,
            outgoingText = R.color.black_color,
            incomingBubble = R.color.alt_white,
            outgoingBubble = R.color.light_outgoing_bubble,
            toolbarText = R.color.white_color,
            messageSendingStatus = R.color.light_icons,
            messageSentStatus = R.color.light_icons,
            messageDeliveredStatus = R.color.light_icons,
            messageReadStatus = R.color.light_icons,
            messageFailedStatus = R.color.light_icons,
            incomingLink = R.color.light_links,
            outgoingLink = R.color.light_links,
            toolbar = R.color.light_main,
            statusBar = R.color.light_statusbar,
            menuItem = R.color.light_main
        )
        val darkColors = ChatColors(
            main = R.color.dark_main,
            searchingProgressLoader = R.color.dark_main,
            bodyIconsTint = R.color.dark_main,
            chatBackground = R.color.dark_chat_background,
            incomingText = R.color.dark_messages_text,
            incomingTimeText = R.color.dark_time_text,
            outgoingText = R.color.dark_messages_text,
            incomingBubble = R.color.dark_incoming_bubble,
            outgoingBubble = R.color.dark_outgoing_bubble,
            toolbarText = R.color.white_color,
            outgoingTimeText = R.color.dark_time_text,
            messageSendingStatus = R.color.dark_icons,
            messageSentStatus = R.color.dark_icons,
            messageDeliveredStatus = R.color.dark_icons,
            messageReadStatus = R.color.dark_icons,
            messageFailedStatus = R.color.dark_icons,
            incomingLink = R.color.dark_links,
            outgoingLink = R.color.dark_links,
            statusBar = R.color.alt_threads_chat_status_bar,
            toolbar = R.color.dark_main,
            toolbarContextMenu = R.color.alt_threads_chat_context_menu,
            menuItem = R.color.alt_threads_chat_toolbar_menu_item_black,
            systemMessage = R.color.dark_system_text,
            welcomeScreenTitleText = R.color.dark_system_text,
            welcomeScreenSubtitleText = R.color.dark_system_text,
            chatErrorScreenImageTint = R.color.white_color,
            searchResultsItemRightArrowTint = R.color.white_color,
            searchResultBackground = R.color.dark_chat_background,
            searchResultsDivider = R.color.dark_time_text,
            searchLoaderTint = R.color.white_color,
            searchResultsItemMessageText = R.color.dark_messages_text,
            searchResultsItemNameText = R.color.white_color,
            searchResultsItemDateText = R.color.dark_messages_text,
            buttonSurveyTextButtonColor = R.color.dark_messages_text,
            surveyButtonBackgroundTintColor = R.color.dark_incoming_bubble,
            quickReplyButtonBackgroundTint = R.color.dark_incoming_bubble,
            quickRepliesText = R.color.dark_messages_text
        )
        val lightImages = ChatImages(
            backBtn = R.drawable.alt_ic_arrow_back_24dp,
            scrollDownButtonIcon = R.drawable.alt_threads_scroll_down_icon_light
        )
        val darkImages = ChatImages(
            backBtn = R.drawable.alt_ic_arrow_back_24dp,
            scrollDownButtonIcon = R.drawable.alt_threads_scroll_down_icon_black
        )
        val lightChatComponents = ChatComponents(
            applicationContext,
            colors = lightColors,
            images = lightImages
        ).apply {
            navigationBarStyle = navigationBarStyle.copy(closeButtonEnabled = false)
        }

        val darkChatComponents = ChatComponents(
            applicationContext,
            colors = darkColors,
            images = darkImages
        ).apply {
            navigationBarStyle = navigationBarStyle.copy(closeButtonEnabled = false)
        }

        // chatDarkTheme = ChatTheme(darkChatComponents) // темная тема
        // chatLightTheme = ChatTheme(lightChatComponents) // светлая тема

        // Пример альтернативной инициализации
        /*chatLightTheme = ChatTheme(
            lightChatComponents.apply {
                inputTextComponent.inputMessageColor = R.color.alt_blue
            }
        ) // создайте инстанс, задав основные компоненты. В данном случае цвет текста во всех инпутах изменен*/

        // chatLightTheme = ChatTheme(applicationContext, lightColors, lightImages) // можно передать только нужные ресуры

        val lightFlows = ChatFlows(lightChatComponents).apply {
            chatFlow.outcomeMessages.imagesTimeTextWhenError = chatFlow.outcomeMessages.imagesTimeText.copy(
                textColor = R.color.white_color
            )
            chatFlow.outcomeMessages.timeTextWhenError = chatFlow.outcomeMessages.timeText.copy(
                textColor = R.color.white_color
            )
            chatFlow.outcomeMessages.openGraphTimeTextWhenError = chatFlow.outcomeMessages.openGraphTimeText.copy(
                textColor = R.color.white_color
            )
            chatFlow.incomeMessages.imagesTimeTextWhenError = chatFlow.incomeMessages.imagesTimeText.copy(
                textColor = R.color.white_color
            )
            chatFlow.incomeMessages.timeTextWhenError = chatFlow.incomeMessages.timeText.copy(
                textColor = R.color.white_color
            )
            chatFlow.incomeMessages.openGraphTimeTextWhenError = chatFlow.incomeMessages.openGraphTimeText.copy(
                textColor = R.color.white_color
            )
        }
        chatLightTheme = ChatTheme(lightFlows) // создайте инстанс, переопределив точечно нужные элементы

        val darkFlows = ChatFlows(darkChatComponents).apply {
            chatFlow.outcomeMessages.imagesTimeTextWhenError = chatFlow.outcomeMessages.imagesTimeText.copy(
                textColor = R.color.white_color
            )
            chatFlow.outcomeMessages.timeTextWhenError = chatFlow.outcomeMessages.timeText.copy(
                textColor = R.color.white_color
            )
            chatFlow.outcomeMessages.openGraphTimeTextWhenError = chatFlow.outcomeMessages.openGraphTimeText.copy(
                textColor = R.color.white_color
            )
            chatFlow.incomeMessages.imagesTimeTextWhenError = chatFlow.incomeMessages.imagesTimeText.copy(
                textColor = R.color.white_color
            )
            chatFlow.incomeMessages.timeTextWhenError = chatFlow.incomeMessages.timeText.copy(
                textColor = R.color.white_color
            )
            chatFlow.incomeMessages.openGraphTimeTextWhenError = chatFlow.incomeMessages.openGraphTimeText.copy(
                textColor = R.color.white_color
            )
        }
        chatDarkTheme = ChatTheme(darkFlows) // создайте инстанс, переопределив точечно нужные элементы
    }

    fun initChatCenterUI(
        serverConfig: ServerConfig? = null,
        chatConfig: ChatConfig? = null,
        apiVersion: ChatApiVersion = ChatApiVersion.defaultApiVersionEnum
    ) {
        val loggerConfig = ChatLoggerConfig(
            applicationContext,
            logFileSize = 50,
            logInterceptor = object : ChatLoggerConfig.LogInterceptor {
                override fun addLogEvent(logLevel: ChatLogLevel, logText: String) {
                    coroutineScope.launch(Dispatchers.IO) {
                        val pattern = "yyyy-MM-dd HH:mm:ss.SSS"
                        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
                        val time = simpleDateFormat.format(Date(System.currentTimeMillis()))
                        LogViewModel.logsData.add(LogModel(time, logLevel, logText))
                    }
                }
            }
        )

        val server = serverConfig ?: try {
            serversProvider.getSelectedServer() ?: serversProvider.readServersFromFile().first()
        } catch (exc: Exception) {
            Toast.makeText(
                applicationContext,
                applicationContext.getString(R.string.no_servers),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val transportConfig = ChatTransportConfig(
            server.serverBaseUrl ?: "",
            server.threadsGateUrl ?: "",
            server.datastoreUrl ?: "",
            hashMapOf(),
            apiVersion = apiVersion
        )
        val certificates = server.trustedSSLCertificates?.map { ChatSSLCertificate(it) }?.toTypedArray() ?: arrayOf()
        val networkConfig = ChatNetworkConfig(
            HTTPConfig(),
            WSConfig(),
            SSLPinningConfig(certificates, server.allowUntrustedSSLCertificate)
        )

        val settingsPreferences = getSharedPreferences(settingsPreferencesName, Context.MODE_PRIVATE)
        val isSearchEnabled = settingsPreferences.getBoolean(settingsKeySearch, true)
        val isLinkPreviewEnabled = settingsPreferences.getBoolean(settingsKeyOpenGraph, true)
        val isVoiceRecordingEnabled = settingsPreferences.getBoolean(settingsKeyVoiceMessages, true)
        val isKeepWebSocketActive = settingsPreferences.getBoolean(settingsKeyKeepWebSocket, false)
        val isKeepWebSocketActiveDuringSession = settingsPreferences.getBoolean(settingsKeyKeepWebSocketDuringSession, false)

        chatConfig?.apply {
            searchEnabled = isSearchEnabled
            linkPreviewEnabled = isLinkPreviewEnabled
            voiceRecordingEnabled = isVoiceRecordingEnabled
            keepWebSocketActive = isKeepWebSocketActive
            keepSocketActiveDuringOperatorSession = isKeepWebSocketActiveDuringSession
        }

        val chatConf = chatConfig ?: ChatConfig(
            transportConfig,
            networkConfig,
            searchEnabled = isSearchEnabled,
            linkPreviewEnabled = isLinkPreviewEnabled,
            voiceRecordingEnabled = isVoiceRecordingEnabled,
            keepWebSocketActive = isKeepWebSocketActive,
            keepSocketActiveDuringOperatorSession = isKeepWebSocketActiveDuringSession,
            autoScrollToLatest = true
        ).apply {
            userInputEnabled = server.isInputEnabled
        }

        chatCenterUI = ChatCenterUI(applicationContext, loggerConfig).apply {
            theme = chatLightTheme
            darkTheme = chatDarkTheme
            init(server.threadsGateProviderUid ?: "", server.appMarker ?: "", chatConf)
        }
    }

    private fun startAppCenter() {
        if (edna.chatcenter.demo.BuildConfig.DEBUG.not()) {
            System.getenv("APP_CENTER_KEY")?.let { appCenterKey ->
                AppCenter.start(
                    this,
                    appCenterKey,
                    Analytics::class.java,
                    Crashes::class.java
                )
            }
        }
    }
}

const val ednaMockScheme = "http"
const val ednaMockHost = "localhost"
const val ednaMockPort = 8080
const val ednaMockUrl = "$ednaMockScheme://$ednaMockHost:$ednaMockPort/"
const val ednaMockThreadsGateUrl = "ws://$ednaMockHost:$ednaMockPort/gate/socket"
const val ednaMockThreadsGateProviderUid = "TEST_93jLrtnipZsfbTddRfEfbyfEe5LKKhTl"
const val ednaMockAllowUntrustedSSLCertificate = true
