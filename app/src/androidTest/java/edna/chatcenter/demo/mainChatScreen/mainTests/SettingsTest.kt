package edna.chatcenter.demo.mainChatScreen.mainTests

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.materialswitch.MaterialSwitch
import edna.chatcenter.demo.BaseTestCase
import edna.chatcenter.demo.R
import edna.chatcenter.demo.TestMessages
import edna.chatcenter.demo.appCode.activity.MainActivity
import edna.chatcenter.demo.kaspressoSreens.ChatMainScreen
import edna.chatcenter.demo.kaspressoSreens.DemoLoginScreen
import edna.chatcenter.demo.kaspressoSreens.SettingsScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsTest : BaseTestCase() {
    private val intent = Intent(context, MainActivity::class.java)

    @get:Rule
    internal val activityRule = ActivityScenarioRule<MainActivity>(intent)

    init {
        applyDefaultUserToDemoApp()
        prepareWsMocks()
        prepareHttpMocks()
    }

    @Before
    override fun before() {
        super.before()
        clearLogcat()
    }

    @Test
    fun testUnreadMessagesCounter() {
        openSettingsPage()
        SettingsScreen {
            if (!isUnreadMessagesCounterEnabled()) {
                keepSocketActiveSetting.click()
            }
        }
        returnFromSettingsPage()
        openChatFromDemoLoginPage()
        minimizeApp()
        sendMessageToSocket(TestMessages.operatorHelloMessage)

        // Ждем пока пробросится лог
        Thread.sleep(500)
        val logs = device.logcat.readLogcatRows()
        assert(logs.any { it.contains("New unread messages count: 1") })
    }

    @Test
    fun testUnreadMessagesCounterWhenDuringSession() {
        openSettingsPage()
        SettingsScreen {
            if (!isUnreadMessagesCounterEnabled()) {
                keepSocketActiveDuringSessionSetting.click()
            }
        }
        returnFromSettingsPage()
        openChatFromDemoLoginPage()
        minimizeApp()
        sendMessageToSocket(TestMessages.operatorHelloMessage)

        // Ждем пока пробросится лог
        Thread.sleep(500)
        val logs = device.logcat.readLogcatRows()
        assert(logs.any { it.contains("New unread messages count: 1") })
    }

    @Test
    fun testOpenGraphInvisibleFromOperator() {
        openSettingsPage()
        SettingsScreen {
            if (isOpenGraphSettingEnabled()) {
                openGraphSetting.click()
            }
        }
        returnFromSettingsPage()
        openChatFromDemoLoginPage()
        sendMessageToSocket(TestMessages.operatorEdnaRuMessage)
        Thread.sleep(700)
        val logs = device.logcat.readLogcatRows()
        assert(!logs.any { it.contains(" OGParser Result") })
    }

    @Test
    fun testOpenGraphInvisibleFromClient() {
        openSettingsPage()
        SettingsScreen {
            if (isOpenGraphSettingEnabled()) {
                openGraphSetting.click()
            }
        }
        returnFromSettingsPage()
        openChatFromDemoLoginPage()
        sendCustomMessageFromUser("edna.ru")
        Thread.sleep(700)
        val logs = device.logcat.readLogcatRows()
        assert(!logs.any { it.contains(" OGParser Result") })
    }

    @Test
    fun testOpenGraphIsVisibleFromOperator() {
        openSettingsPage()
        SettingsScreen {
            if (!isOpenGraphSettingEnabled()) {
                openGraphSetting.click()
            }
        }
        returnFromSettingsPage()
        openChatFromDemoLoginPage()
        sendMessageToSocket(TestMessages.operatorEdnaRuMessage)
        Thread.sleep(700)
        val logs = device.logcat.readLogcatRows()
        assert(logs.any { it.contains(" OGParser Result") })
    }

    @Test
    fun testOpenGraphIsVisibleFromClient() {
        openSettingsPage()
        SettingsScreen {
            if (!isOpenGraphSettingEnabled()) {
                openGraphSetting.click()
            }
        }
        returnFromSettingsPage()
        openChatFromDemoLoginPage()
        sendCustomMessageFromUser("edna.ru")
        Thread.sleep(700)
        val logs = device.logcat.readLogcatRows()
        assert(logs.any { it.contains(" OGParser Result") })
    }

    @Test
    fun testVoiceMessagesBtnIsVisible() {
        openSettingsPage()
        SettingsScreen {
            if (!isVoiceMessagesEnabled()) {
                voiceSetting.click()
            }
        }
        returnFromSettingsPage()
        openChatFromDemoLoginPage()
        ChatMainScreen {
            recordButton.isDisplayed()
        }
    }

    @Test
    fun testVoiceMessagesBtnIsInvisible() {
        openSettingsPage()
        SettingsScreen {
            if (isVoiceMessagesEnabled()) {
                voiceSetting.click()
            }
        }
        returnFromSettingsPage()
        openChatFromDemoLoginPage()
        ChatMainScreen {
            recordButton.isNotDisplayed()
        }
    }

    @Test
    fun testSearchBtnIsInvisible() {
        openSettingsPage()
        SettingsScreen {
            if (isSearchEnabled()) {
                searchSetting.click()
            }
        }
        returnFromSettingsPage()
        openChatFromDemoLoginPage()
        ChatMainScreen {
            searchButton.isNotDisplayed()
        }
    }

    @Test
    fun testSearchBtnIsVisible() {
        openSettingsPage()
        SettingsScreen {
            if (!isSearchEnabled()) {
                searchSetting.click()
            }
        }
        returnFromSettingsPage()
        openChatFromDemoLoginPage()
        ChatMainScreen {
            searchButton.isDisplayed()
        }
    }

    private fun openSettingsPage() {
        DemoLoginScreen {
            settingsButton.click()
        }
    }

    private fun returnFromSettingsPage() {
        SettingsScreen {
            backButton.click()
        }
    }

    private fun minimizeApp() {
        device.uiDevice.pressHome()
    }

    private fun isUnreadMessagesCounterEnabled() = isSettingEnabled(R.id.webSocketSwitcher)

    private fun isOpenGraphSettingEnabled() = isSettingEnabled(R.id.openGraphSwitcher)

    private fun isVoiceMessagesEnabled() = isSettingEnabled(R.id.voiceSwitcher)

    private fun isSearchEnabled() = isSettingEnabled(R.id.searchSwitcher)

    private fun isSettingEnabled(id: Int): Boolean {
        var isChecked = true
        onView(withId(id)).check { view, _ ->
            isChecked = (view as MaterialSwitch).isChecked
        }
        return isChecked
    }
}
