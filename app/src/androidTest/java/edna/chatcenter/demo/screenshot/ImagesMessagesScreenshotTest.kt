package edna.chatcenter.demo.screenshot

import androidx.test.ext.junit.runners.AndroidJUnit4
import edna.chatcenter.demo.kaspressoSreens.ChatMainScreen
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ImagesMessagesScreenshotTest : BaseScreenshotTestCase() {
    @ScreenshotTest
    @Test
    fun imagesMessagesScreenshotTextAtEnd() {
        openDemoExample(stringsProvider.images)
        scrollToRecyclerViewEnd()
        Thread.sleep(2000)
        testActivityScreenshot()
    }

    @ScreenshotTest
    @Test
    fun imagesMessagesScreenshotTextAtStart() {
        openDemoExample(stringsProvider.images)
        ChatMainScreen {
            chatItemsRecyclerView { scrollToStart() }
        }
        Thread.sleep(2000)
        testActivityScreenshot()
    }

    @ScreenshotTest
    @Test
    fun imagesMessagesScreenshotTextAtTheMiddle() {
        openDemoExample(stringsProvider.images)
        scrollToRecyclerViewMiddle()
        Thread.sleep(2000)
        testActivityScreenshot()
    }
}
