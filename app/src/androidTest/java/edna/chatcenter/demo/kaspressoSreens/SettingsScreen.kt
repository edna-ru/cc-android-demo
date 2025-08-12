package edna.chatcenter.demo.kaspressoSreens

import com.kaspersky.kaspresso.screens.KScreen
import edna.chatcenter.demo.R
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.switch.KSwitch

object SettingsScreen : KScreen<SettingsScreen>() {
    override val layoutId: Int = R.layout.fragment_settings
    override val viewClass: Class<*> = SettingsScreen::class.java

    val keepSocketActiveSetting = KSwitch { withId(R.id.webSocketSwitcher) }
    val keepSocketActiveDuringSessionSetting = KSwitch { withId(R.id.webSocketDuringSessionSwitcher) }
    val openGraphSetting = KSwitch { withId(R.id.openGraphSwitcher) }
    val voiceSetting = KSwitch { withId(R.id.voiceSwitcher) }
    val searchSetting = KSwitch { withId(R.id.searchSwitcher) }
    val backButton = KImageView { withId(R.id.backButton) }
}
