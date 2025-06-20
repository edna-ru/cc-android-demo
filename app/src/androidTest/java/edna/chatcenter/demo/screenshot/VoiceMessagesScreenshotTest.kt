package edna.chatcenter.demo.screenshot

import androidx.test.ext.junit.runners.AndroidJUnit4
import edna.chatcenter.demo.kaspressoSreens.ChatMainScreen
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VoiceMessagesScreenshotTest : BaseScreenshotTestCase() {
    @ScreenshotTest
    @Test
    fun voiceMessagesScreenshotTextAtEnd() {
        openDemoExample(stringsProvider.voiceMessages)
        scrollToRecyclerViewEnd()
        Thread.sleep(2000)
        testActivityScreenshot()
    }

    @ScreenshotTest
    @Test
    fun voiceMessagesScreenshotTextAtStart() {
        openDemoExample(stringsProvider.voiceMessages)
        ChatMainScreen {
            chatItemsRecyclerView { scrollToStart() }
        }
        Thread.sleep(2000)
        testActivityScreenshot()
    }
}
