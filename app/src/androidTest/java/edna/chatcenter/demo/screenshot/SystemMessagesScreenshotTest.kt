package edna.chatcenter.demo.screenshot

import androidx.test.ext.junit.runners.AndroidJUnit4
import edna.chatcenter.demo.kaspressoSreens.ChatMainScreen
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SystemMessagesScreenshotTest : BaseScreenshotTestCase() {
    @ScreenshotTest
    @Test
    fun systemMessagesScreenshotTextAtEnd() {
        openDemoExample(stringsProvider.systemMessages)
        scrollToRecyclerViewEnd()
        Thread.sleep(2000)
        testActivityScreenshot()
    }

    @ScreenshotTest
    @Test
    fun systemMessagesScreenshotTextAtStart() {
        openDemoExample(stringsProvider.systemMessages)
        ChatMainScreen {
            chatItemsRecyclerView { scrollToStart() }
        }
        Thread.sleep(2000)
        testActivityScreenshot()
    }
}
