package edna.chatcenter.demo.mainChatScreen.mainTests

import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import edna.chatcenter.demo.BaseTestCase
import edna.chatcenter.demo.TestMessages
import edna.chatcenter.demo.appCode.activity.MainActivity
import edna.chatcenter.demo.assert
import edna.chatcenter.demo.kaspressoSreens.ChatAppFragmentScreen
import edna.chatcenter.demo.kaspressoSreens.ChatMainScreen
import edna.chatcenter.demo.kaspressoSreens.DemoLoginScreen
import edna.chatcenter.demo.waitListForNotEmpty
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Проверяет блокировку поля ввода при получении от бота быстрых ответов-кнопок с серверной
 * настройкой `settings.blockInput:true`: пока такие быстрые ответы показаны в чате, поле ввода
 * должно быть заблокировано, а пользователю доступен только выбор одной из кнопок
 *
 * Гонка воспроизводится только на реальном адаптере: юнит-тесты на mock-фрагменте
 * (`ChatControllerQuickRepliesBlockInputTest`) гоняют `ChatController` с полностью синхронным
 * списком `elements`, поэтому не ловят гонку с `AsyncListDiffer`,
 * из-за которой пересчёт состояния поля ввода успевал отработать на ещё
 * пустом списке.
 *
 * Эти два теста прогоняют сценарий на настоящем `ChatFragment`
 */
@RunWith(AndroidJUnit4::class)
class QuickRepliesBlockInputTest : BaseTestCase(true) {

    private val intent =
        Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)

    @get:Rule
    internal val activityRule = ActivityScenarioRule<MainActivity>(intent)

    init {
        applyDefaultUserToDemoApp()
        prepareWsMocks()
    }

    /**
     * Сценарий первого входа в чат: сообщение бота с быстрыми ответами и
     * `settings.blockInput:true` приходит вживую по WebSocket уже после того, как (пустая)
     * история загружена и адаптер инициализирован
     *
     * Проверяется, что поле ввода после этого заблокировано
     */
    @Test
    fun whenBlockingQuickRepliesArriveViaSocketOnFirstEntry_thenInputIsDisabled() {
        prepareHttpMocks()
        openChatFromDemoLoginPage()

        sendMessageToSocket(message = TestMessages.botWithBlockingQuickRepliesSocketMessage)

        ChatMainScreen {
            chatItemsRecyclerView {
                waitListForNotEmpty(5000)
                assert("Список сообщений должен содержать быстрые ответы бота") {
                    hasDescendant { containsText("Список часто задаваемых вопросов") }
                }
            }

            inputEditView {
                assert(
                    "Поле ввода должно быть заблокировано, пока показаны блокирующие " +
                            "быстрые ответы (settings.blockInput=true)"
                ) {
                    isDisabled()
                }
            }
        }
    }

    /**
     * Сценарий перезахода в чат: сообщение с быстрыми ответами и `settings.blockInput:true`
     * приходит не по сокету, а уже частью истории (тред не пуст)
     *
     * При открытии чата история подгружается асинхронно, и есть окно, в котором пересчёт
     * состояния поля ввода успевает отработать на ещё не наполненном `AsyncListDiffer`-ом списке,
     * не находит блокирующее сообщение и ошибочно включает поле ввода
     *
     * Проверяется, что поле ввода заблокировано сразу после загрузки истории и остаётся
     * заблокированным после переключения на другую вкладку и возврата на вкладку "Чат"
     */
    @Test
    fun whenBlockingQuickRepliesArriveFromHistoryAndChatTabIsReopened_thenInputStaysDisabled() {
        prepareHttpMocks(historyAnswer = TestMessages.historyWithBlockingQuickRepliesMessage)

        DemoLoginScreen { loginButton { click() } }
        openChatInTab()

        ChatMainScreen.chatItemsRecyclerView {
            waitListForNotEmpty(5000)
            assert("Список сообщений должен содержать быстрые ответы бота из истории") {
                hasDescendant { containsText("Список часто задаваемых вопросов") }
            }
        }

        // Ждём, пока пересчёты состояния поля ввода, вызванные загрузкой истории, осядут.
        Thread.sleep(700)
        ChatMainScreen.inputEditView {
            assert(
                "Поле ввода должно быть заблокировано сразу после загрузки истории " +
                        "с быстрыми ответами (settings.blockInput=true)"
            ) {
                isDisabled()
            }
        }

        openLogInTab()
        Thread.sleep(200)
        openChatInTab()

        ChatMainScreen.chatItemsRecyclerView { waitListForNotEmpty(5000) }
        // Даём тому же окну гонки (см. KDoc метода) повториться при повторном открытии вкладки.
        Thread.sleep(700)

        ChatMainScreen.inputEditView {
            assert(
                "После возврата на вкладку \"Чат\" поле ввода должно остаться " +
                        "заблокированным, а не разблокироваться из-за гонки с AsyncListDiffer"
            ) {
                isDisabled()
            }
        }
    }

    /**
     * Сценарий: сообщение бота с блокирующими быстрыми ответами (`settings.blockInput:true`)
     * приходит частью истории, а следом в той же истории уже есть ответ пользователя на эти
     * кнопки — то есть кнопки отвечены и в чате больше не показаны
     *
     * Проверяется, что поле ввода в этом случае активно, а не заблокировано: отвеченные
     * (спрятанные) кнопки не должны держать поле ввода заблокированным
     */
    @Test
    fun whenBlockingQuickRepliesAnsweredInHistory_thenInputIsEnabled() {
        prepareHttpMocks(historyAnswer = TestMessages.historyWithAnsweredBlockingQuickRepliesMessage)

        DemoLoginScreen { loginButton { click() } }
        openChatInTab()

        ChatMainScreen.chatItemsRecyclerView {
            waitListForNotEmpty(5000)
            assert("Список сообщений должен содержать ответ пользователя на быстрые ответы бота") {
                hasDescendant { containsText("Список часто задаваемых вопросов") }
            }
        }

        // Ждём, пока пересчёты состояния поля ввода, вызванные загрузкой истории, осядут.
        Thread.sleep(700)
        ChatMainScreen.inputEditView {
            assert("Поле ввода должно быть активно, так как быстрые ответы уже отвечены") {
                isEnabled()
            }
        }
    }

    private fun openChatInTab() {
        ChatAppFragmentScreen { tabLayout { selectTab(index = 1) } }
    }

    private fun openLogInTab() {
        ChatAppFragmentScreen { tabLayout { selectTab(index = 2) } }
    }
}
