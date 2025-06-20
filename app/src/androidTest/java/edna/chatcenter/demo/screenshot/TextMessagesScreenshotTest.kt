package edna.chatcenter.demo.screenshot

import androidx.test.ext.junit.runners.AndroidJUnit4
import edna.chatcenter.demo.kaspressoSreens.ChatMainScreen
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TextMessagesScreenshotTest : BaseScreenshotTestCase() {

    @ScreenshotTest
    @Test
    fun textMessagesScreenshotTextAtEnd() {
        openDemoExample(stringsProvider.textMessages)
        scrollToRecyclerViewEnd()
        Thread.sleep(2000)
        testActivityScreenshot()
    }

    @ScreenshotTest
    @Test
    fun textMessagesScreenshotTextAtStart() {
        openDemoExample(stringsProvider.textMessages)
        ChatMainScreen {
            chatItemsRecyclerView { scrollToStart() }
        }
        Thread.sleep(2000)
        testActivityScreenshot()
    }

    @ScreenshotTest
    @Test
    fun textMessagesScreenshotTextAtTheMiddle() {
        openDemoExample(stringsProvider.textMessages)
        scrollToRecyclerViewMiddle()
        Thread.sleep(2000)
        testActivityScreenshot()
    }
}
