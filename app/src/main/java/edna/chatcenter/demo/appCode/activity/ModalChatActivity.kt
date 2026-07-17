package edna.chatcenter.demo.appCode.activity

import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import edna.chatcenter.core.annotation.OpenWay
import edna.chatcenter.demo.R
import edna.chatcenter.demo.appCode.business.SettingsStorage
import edna.chatcenter.demo.integrationCode.EdnaChatCenterApplication
import edna.chatcenter.ui.visual.fragments.ChatFragment
import org.koin.android.ext.android.inject

class ModalChatActivity : AppCompatActivity() {

    var fragment: ChatFragment? = null

    private val settingsStorage: SettingsStorage by inject()

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (fragment?.onBackPressed() == true) {
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Режим edge-to-edge включается согласно настройке приложения и конфигурации SDK. Если
        // настройка неактивна для текущей версии Android, окно остаётся обычным, а системные
        // отступы обрабатываются платформой.
        if (settingsStorage.edgeToEdgeMode.isActiveOn()) {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
                navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
            )
            @Suppress("DEPRECATION")
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modal_chat)

        onBackPressedDispatcher.addCallback(this, backCallback)

        fragment = ChatFragment.newInstance(OpenWay.DEFAULT)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.chatFragmentContainer, getChatFragment())
            .commitAllowingStateLoss()
    }

    private fun getChatFragment(): ChatFragment {
        val chatCenterUI = (applicationContext as? EdnaChatCenterApplication)?.chatCenterUI
        return chatCenterUI?.getChatFragment() ?: ChatFragment.newInstance(OpenWay.DEFAULT)
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatFragment.weakInstance.clear()
    }
}
