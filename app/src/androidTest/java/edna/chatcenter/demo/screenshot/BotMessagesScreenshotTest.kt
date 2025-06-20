package edna.chatcenter.demo.screenshot

import androidx.test.ext.junit.runners.AndroidJUnit4
import edna.chatcenter.demo.kaspressoSreens.ChatMainScreen
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BotMessagesScreenshotTest : BaseScreenshotTestCase() {

    @ScreenshotTest
    @Test
    fun botMessagesScreenshotTextAtStart() {
        openDemoExample(stringsProvider.chatWithBot)
        scrollToRecyclerViewEnd()
        Thread.sleep(2000)
        testActivityScreenshot()
    }

    @ScreenshotTest
    @Test
    fun botMessagesScreenshotTextAtEnd() {
        openDemoExample(stringsProvider.chatWithBot)
        Thread.sleep(2000)
        ChatMainScreen {
            chatItemsRecyclerView {
                isVisible()
            }
            scrollDownBtn { click() }
        }
        Thread.sleep(2000)
        testActivityScreenshot()
    }
}
