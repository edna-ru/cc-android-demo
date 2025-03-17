package edna.chatcenter.demo.mainChatScreen.mainTests

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import edna.chatcenter.demo.BaseTestCase
import edna.chatcenter.demo.TestMessages
import edna.chatcenter.demo.appCode.activity.MainActivity
import edna.chatcenter.demo.assert
import edna.chatcenter.demo.kaspressoSreens.ChatAppFragmentScreen
import edna.chatcenter.demo.kaspressoSreens.ChatMainScreen
import edna.chatcenter.demo.kaspressoSreens.DemoLoginScreen
import edna.chatcenter.demo.kaspressoSreens.StartChatScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Date

@RunWith(AndroidJUnit4::class)
class TabsTest : BaseTestCase() {
    private val intent = Intent(context, MainActivity::class.java)

    @get:Rule
    internal val activityRule = ActivityScenarioRule<MainActivity>(intent)

    init {
        applyDefaultUserToDemoApp()
        prepareWsMocks()
    }

    @Test
    fun openChatInTabsAndEnsure() {
        prepareHttpMocks()
        enterDemo()
        openChatInTab()
        doMainTest()
    }

    @Test
    fun switchTabsSeveralTimesAndEnsure() {
        prepareHttpMocks()
        enterDemo()
        openChatInTab()
        checkIsMessagesExists()
        openLogInTab()
        Thread.sleep(100)
        openChatInTab()
        Thread.sleep(500)
        doMainTest()
    }

    @Test
    fun switchBetweenActivityAndTabsAndEnsure() {
        prepareHttpMocks()
        enterDemo()
        openChatInTab()
        checkIsMessagesExists()
        Thread.sleep(100)
        openMainTab()
        openChatInActivity()
        Thread.sleep(200)
        Espresso.pressBack()
        openChatInTab()
        Thread.sleep(200)

        sendHelloMessageFromUser()
        checkIsMessagesExists()

        ChatMainScreen.chatItemsRecyclerView {
            assert("Список сообщений должен содержать текст: \"${helloTextToSend}\"") {
                hasDescendant { containsText(helloTextToSend) }
            }
        }
    }

    private fun enterDemo() {
        DemoLoginScreen {
            loginButton {
                click()
            }
        }
    }

    private fun openChatInTab() {
        ChatAppFragmentScreen {
            tabLayout {
                selectTab(1)
            }
        }
    }

    private fun openLogInTab() {
        ChatAppFragmentScreen {
            tabLayout {
                selectTab(2)
            }
        }
    }

    private fun openMainTab() {
        ChatAppFragmentScreen {
            tabLayout {
                selectTab(0)
            }
        }
    }

    private fun openChatInActivity() {
        StartChatScreen {
            openChatButton {
                click()
            }
        }
    }

    private fun doMainTest() {
        sendHelloMessageFromUser()
        checkIsMessagesExists()

        ChatMainScreen.chatItemsRecyclerView {
            assert("Список сообщений должен содержать текст: \"${helloTextToSend}\"") {
                hasDescendant { containsText(helloTextToSend) }
            }
        }

        sendMessageFromOperator()

        ChatMainScreen {
            chatItemsRecyclerView {
                assert("Список сообщений должен быть видим") { isVisible() }
                val textToCheck = "привет!"
                assert("Список сообщений должен содержать текст: \"${textToCheck}\"") {
                    hasDescendant { containsText(textToCheck) }
                }
            }
        }
    }

    private fun checkIsMessagesExists() {
        ChatMainScreen.chatItemsRecyclerView {
            assert("Список сообщений должен быть видим") { isVisible() }
        }
    }

    private fun sendMessageFromOperator() {
        val currentTimeMs = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd")
        val timeFormat = SimpleDateFormat("HH:mm:ss")
        val currentDateObj = Date(currentTimeMs)
        val currentDate = dateFormat.format(currentDateObj)
        val currentTime = timeFormat.format(currentDateObj)

        val operatorMessage = TestMessages.operatorHelloMessage
            .replace("2023-09-25", currentDate)
            .replace("13:07:29", currentTime)

        sendMessageToSocket(operatorMessage)
    }
}
