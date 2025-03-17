package edna.chatcenter.demo.mainChatScreen.mainTests

import android.app.Activity
import android.app.Instrumentation
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasData
import androidx.test.espresso.intent.matcher.IntentMatchers.isInternal
import androidx.test.espresso.intent.rule.IntentsRule
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import edna.chatcenter.demo.BaseTestCase
import edna.chatcenter.demo.R
import edna.chatcenter.demo.RecyclerViewMatcher
import edna.chatcenter.demo.TestMessages
import edna.chatcenter.demo.appCode.activity.MainActivity
import edna.chatcenter.demo.assert
import edna.chatcenter.demo.kaspressoSreens.ChatMainScreen
import edna.chatcenter.demo.waitListForNotEmpty
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

@RunWith(AndroidJUnit4::class)
class TextMessagesTest : BaseTestCase(
    timeoutInSeconds = 10
) {
    private val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)

    @get:Rule
    internal val activityRule = ActivityScenarioRule<MainActivity>(intent)

    @get:Rule
    val intentsTestRule = IntentsRule()

    init {
        applyDefaultUserToDemoApp()
        prepareWsMocks()
    }

    @Before
    override fun before() {
        super.before()
        intending(not(isInternal())).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_OK,
                null
            )
        )
    }

    @Test
    fun textMessages() {
        prepareHttpMocks()
        openChatFromDemoLoginPage()

        sendHelloMessageFromUser()
        ChatMainScreen.chatItemsRecyclerView {
            assert("Список сообщений должен быть видим") { isVisible() }
            assert("Список сообщений должен содержать текст: \"${helloTextToSend}\"") {
                hasDescendant { containsText(helloTextToSend) }
            }
        }

        sendMessageFromOperator()
        ChatMainScreen {
            chatItemsRecyclerView {
                assert("Список сообщений должен быть видим") { isVisible() }
                val textToCheck = "привет!"
                assert("Список сообщений должен содержать текст: \"${helloTextToSend}\"") {
                    hasDescendant { containsText(textToCheck) }
                }
            }
        }
    }

    @Test
    fun historyTest_Espresso() {
        openMessagesHistory()

        onView(withId(edna.chatcenter.ui.R.id.chatItemsRecycler))
            .perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(1))
        onView(RecyclerViewMatcher(edna.chatcenter.ui.R.id.chatItemsRecycler).atPositionOnView(1, R.id.text))
            .check(matches(withText(containsString("Добрый день! Мы создаем экосистему бизнеса"))))

        onView(withId(edna.chatcenter.ui.R.id.chatItemsRecycler))
            .check(matches(hasDescendant(withText(containsString("Добро пожаловать в наш чат")))))

        onView(withId(edna.chatcenter.ui.R.id.chatItemsRecycler))
            .perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(containsString("Тогда до связи!")))))
        onView(allOf(withId(R.id.text), withText(containsString("Тогда до связи!"))))
            .check(matches(isDisplayed()))

        onView(withId(edna.chatcenter.ui.R.id.chatItemsRecycler))
            .check(matches(hasDescendant(withText(containsString("Отлично! Давайте проверим ваши контакты. Ваш email: info@edna.ru, телефон: +7 (495) 609-60-80. Верно?")))))
        onView(withId(edna.chatcenter.ui.R.id.chatItemsRecycler))
            .check(matches(hasDescendant(withText(containsString("Именно! А еще у нас есть различные каналы коммуникации с клиентами! Подробнее: https://edna.ru/channels/")))))
        onView(withId(edna.chatcenter.ui.R.id.chatItemsRecycler))
            .check(matches(hasDescendant(withText(containsString("Да, все верно")))))
    }

    @Test
    fun operatorTextQuoteTest() {
        prepareHttpMocks()
        openChatFromDemoLoginPage()
        sendMessageFromOperator()

        ChatMainScreen {
            chatItemsRecyclerView {
                assert("Список сообщений должен быть видим") { isVisible() }
                lastChild<ChatMainScreen.ChatRecyclerItem> {
                    assert("Последнее сообщение должно быть видимо") { isVisible() }
                    perform { longClick() }
                }
            }
            replyBtn {
                assert("Кнопка повтора должна быть видимой") { isVisible() }
                click()
            }
            quoteText {
                val textToCheck = "привет!"
                assert("Процитированный текст должен быть видим") { isVisible() }
                assert("Процитированный текст должен содержать строку: \"$textToCheck\"") { hasText(textToCheck) }
            }
            quoteHeader {
                assert("Заголовок цитаты должен быть видим") { isVisible() }
                assert("Заголовок цитаты не должен быть пустым") { hasAnyText() }
            }
            quoteClear {
                assert("Кнопка очистка цитирования должна быть видимой") { isVisible() }
            }
            inputEditView {
                assert("Поле для ввода должно быть видимым") { isVisible() }
                typeText(helloTextToSend)
            }
            sendMessageBtn {
                assert("Кнопка отправки сообщения должна быть видимой") { isVisible() }
                click()
            }
            chatItemsRecyclerView {
                assert("Список должен содержать сообщение: \"$helloTextToSend\"") {
                    hasDescendant { containsText(helloTextToSend) }
                }
            }
        }
    }

    @Test
    fun copyOperatorMessageTest() {
        prepareHttpMocks()
        openChatFromDemoLoginPage()
        sendMessageFromOperator()

        ChatMainScreen {
            chatItemsRecyclerView {
                assert("Список сообщений должен быть видим") { isVisible() }
                lastChild<ChatMainScreen.ChatRecyclerItem> {
                    assert("Последний элемент списка должен быть видим") { isVisible() }
                    perform { longClick() }
                }
            }
            copyBtn {
                assert("Кнопка копирования должна быть видимой и кликабельной", ::isVisible, ::isClickable)
                click()
            }
        }

        Handler(Looper.getMainLooper()).post {
            val clipboard = context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
            val textToCheck = "привет!"
            if (clipboard?.hasPrimaryClip() == true) {
                assert(clipboard.primaryClip?.getItemAt(0)?.text == textToCheck) {
                    "Буфер обмена должен содержать текст: \"$textToCheck\""
                }
            } else {
                assert(false) {
                    "Буфер обмена должен содержать текст: \"$textToCheck\""
                }
            }
        }
    }

    @Test
    fun testIsEmailClickable() {
        openMessagesHistory()

        ChatMainScreen {
            chatItemsRecyclerView {
                childAt<ChatMainScreen.ChatRecyclerItem>(11) {
                    itemText.clickSpanWithText("info@edna.ru")
                }
            }
        }
        intended(
            allOf(
                hasAction(Intent.ACTION_VIEW),
                hasData("mailto:info@edna.ru")
            )
        )
    }

    @Test
    fun testIsPhoneClickable_Espresso() {
        openMessagesHistory()

        checkPhoneNumbersForClick(
            listOf(
                "+79855687102",
                "+7 977 409 27 19",
                "+7-843-552-83-17",
                "+7-811-687-0002",
                "+7 971 971-48-21",
                "+7 (999) 999-99-99"
            ),
            true
        )

        checkPhoneNumbersForClick(
            listOf(
                "+79855687103",
                "+7 977 409 27 20",
                "+7-843-552-83-18",
                "+7-811-687-0003",
                "+7 971 971-48-22",
                "+7 (999) 999-99-98"
            ),
            false
        )
    }

    @Test
    fun testIsUrlClickable_Espresso() {
        openMessagesHistory()

        val userLinks = listOf(
            "https://edna.ru/partners/",
            "https://edna.ru/career/"
        )
        checkLinksForClick(userLinks, isUserMessage = true)

        val operatorLinks = listOf(
            "https://edna.ru/chat-center/",
            "https://edna.ru/newsletters/"
        )
        checkLinksForClick(operatorLinks, isUserMessage = false)
    }

    @Test
    fun testThreadIsClosed() {
        prepareHttpMocks()
        openChatFromDemoLoginPage()
        sendHelloMessageFromUser()
        sendMessageToSocket(TestMessages.threadIsClosed)

        ChatMainScreen.chatItemsRecyclerView {
            val textToCheck = "Диалог завершен. Будем рады проконсультировать вас снова!"
            assert("Список сообщений должен быть видим") { isVisible() }
            assert("Список должен содержать сообщение: \"$textToCheck\"") {
                hasDescendant { containsText(textToCheck) }
            }
        }
    }

    @Test
    fun testClientIsBlocked() {
        prepareHttpMocks()
        openChatFromDemoLoginPage()
        sendHelloMessageFromUser()
        sendMessageToSocket(TestMessages.clientIsBlocked)

        ChatMainScreen.chatItemsRecyclerView {
            val textToCheck = "Вы заблокированы, дальнейшее общение с оператором ограничено"
            assert("Список сообщений должен быть видим") { isVisible() }
            assert("Список должен содержать сообщение: \"$textToCheck\"") {
                hasDescendant { containsText(textToCheck) }
            }
        }
    }

    @Test
    fun testClientTransfer() {
        prepareHttpMocks()
        openChatFromDemoLoginPage()
        sendHelloMessageFromUser()
        sendMessageToSocket(TestMessages.operatorTransfer)
        sendMessageToSocket(TestMessages.operatorAssigned)

        ChatMainScreen {
            chatItemsRecyclerView {
                assert("Список сообщений должен быть видим") { isVisible() }
                assert("Список должен содержать сообщение: \"Для решения вопроса диалог переводится другому оператору\"") {
                    hasDescendant { containsText("Для решения вопроса диалог переводится другому оператору") }
                }
                assert("Список должен содержать сообщение: \"Вам ответит Оператор0 Иванович\"") {
                    hasDescendant { containsText("Вам ответит Оператор0 Иванович") }
                }
            }
            assert("Имя оператора в тулбаре должно быть: \"Оператор0 Иванович\"") {
                toolbarOperatorName.hasText("Оператор0 Иванович")
            }
        }
    }

    @Test
    fun testOperatorWaiting() {
        prepareHttpMocks()
        openChatFromDemoLoginPage()
        sendHelloMessageFromUser()
        sendMessageToSocket(TestMessages.operatorWaiting)

        Thread.sleep(500)

        ChatMainScreen {
            val operatorName = context.getString(edna.chatcenter.ui.R.string.ecc_searching_operator)
            assert("Имя оператора в тулбаре должно быть: \"$operatorName\"") {
                toolbarOperatorName.containsText(operatorName)
            }
            val textToCheck = "Среднее время ожидания ответа составляет 2 минуты"
            chatItemsRecyclerView {
                assert("Список сообщений должен содержать текст: \"$textToCheck\"") {
                    hasDescendant { containsText(textToCheck) }
                }
            }
        }
    }

    @Test
    fun progressbarOnStart() {
        prepareHttpMocks(15000)
        openChatFromDemoLoginPage()
        ChatMainScreen {
            progressBar { isVisible() }
        }
    }

    @Test
    fun whenReceivingButtonsFromBotThenItsOrderedCorrectly_Espresso() {
        prepareHttpMocks(historyAnswer = readTextFileFromRawResourceId(R.raw.history_text_response))
        openChatFromDemoLoginPage()
        sendMessageToSocket(TestMessages.botWithButtonsSocketMessage)

        onView(withId(edna.chatcenter.ui.R.id.chatItemsRecycler))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText(containsString("Список часто задаваемых вопросов")))
                )
            )

        onView(withText(containsString("Список часто задаваемых вопросов")))
            .check(matches(isDisplayed()))
    }

    private fun openMessagesHistory() {
        prepareHttpMocks(historyAnswer = readTextFileFromRawResourceId(R.raw.history_text_response))
        openChatFromDemoLoginPage()
        ChatMainScreen {
            chatItemsRecyclerView {
                waitListForNotEmpty(5000)
                assert("Список сообщений должен быть видим") { isVisible() }
                assert(getSize() > 0) { "Список содержит неверное количество сообщений" }
            }
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

    private fun checkPhoneNumbersForClick(phoneList: List<String>, isUserMessage: Boolean) {
        phoneList.forEach { phone ->
            if (isUserMessage) {
                sendCustomMessageFromUser(phone)
            } else {
                var message = TestMessages.operatorMessageStub
                val currentTimeMillis = System.currentTimeMillis()
                val instant = Instant.ofEpochMilli(currentTimeMillis)
                val time = instant.toString()

                message = message.replace("timeStub", time)
                message = message.replace("messageStub", phone)

                sendMessageToSocket(message)
            }

            onView(withId(edna.chatcenter.ui.R.id.chatItemsRecycler))
                .perform(RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(hasDescendant(withText(containsString(phone)))))

            onView(allOf(withId(R.id.text), withText(containsString(phone))))
                .perform(click())
            val phoneClean = phone
                .replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .replace(".", "")
            intended(allOf(hasAction(Intent.ACTION_VIEW), hasData(Uri.parse("tel:$phoneClean"))))
        }
    }

    private fun checkLinksForClick(linkList: List<String>, isUserMessage: Boolean) {
        linkList.forEach { link ->
            if (isUserMessage) {
                sendCustomMessageFromUser(link)
            } else {
                var message = TestMessages.operatorMessageStub
                val currentTimeMillis = System.currentTimeMillis()
                val time = Instant.ofEpochMilli(currentTimeMillis).toString()
                message = message.replace("timeStub", time)
                message = message.replace("messageStub", link)
                sendMessageToSocket(message)
            }

            onView(withId(edna.chatcenter.ui.R.id.chatItemsRecycler))
                .perform(
                    RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                        hasDescendant(withText(containsString(link)))
                    )
                )

            onView(allOf(withId(R.id.text), withText(containsString(link))))
                .perform(click())

            intended(
                allOf(
                    hasAction(Intent.ACTION_VIEW),
                    hasData(Uri.parse(link))
                )
            )
        }
    }
}
