package edna.chatcenter.demo.screenshot

import androidx.test.ext.junit.runners.AndroidJUnit4
import edna.chatcenter.demo.kaspressoSreens.ChatMainScreen
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ErrorMessagesScreenshotTest : BaseScreenshotTestCase() {
    @ScreenshotTest
    @Test
    fun errorMessagesScreenshotTextAtEnd() {
        openDemoExample(stringsProvider.connectionErrors)
        scrollToRecyclerViewEnd()
        Thread.sleep(2000)
        testActivityScreenshot()
    }

    @ScreenshotTest
    @Test
    fun errorMessagesScreenshotTextAtStart() {
        openDemoExample(stringsProvider.connectionErrors)
        ChatMainScreen {
            chatItemsRecyclerView { scrollToStart() }
        }
        Thread.sleep(2000)
        testActivityScreenshot()
    }
}
