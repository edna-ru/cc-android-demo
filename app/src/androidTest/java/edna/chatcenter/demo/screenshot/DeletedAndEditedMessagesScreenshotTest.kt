package edna.chatcenter.demo.screenshot

import androidx.test.ext.junit.runners.AndroidJUnit4
import edna.chatcenter.demo.kaspressoSreens.ChatMainScreen
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DeletedAndEditedMessagesScreenshotTest : BaseScreenshotTestCase() {

    @ScreenshotTest
    @Test
    fun editedMessagesScreenshotTextAtEnd() {
        openDemoExample(stringsProvider.chatWithEditAndDeletedMessages)
        scrollToRecyclerViewEnd()
        Thread.sleep(2000)
        testActivityScreenshot()
    }

    @ScreenshotTest
    @Test
    fun editedMessagesScreenshotTextAtStart() {
        openDemoExample(stringsProvider.chatWithEditAndDeletedMessages)
        ChatMainScreen {
            chatItemsRecyclerView { scrollToStart() }
        }
        Thread.sleep(2000)
        testActivityScreenshot()
    }
}
