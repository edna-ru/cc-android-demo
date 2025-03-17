package edna.chatcenter.demo.appCode.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import edna.chatcenter.demo.R
import edna.chatcenter.demo.integrationCode.EdnaChatCenterApplication
import edna.chatcenter.ui.visual.fragments.ChatFragment

class ModalChatActivity : AppCompatActivity() {
    var fragment: ChatFragment? = null

    @SuppressLint("CommitTransaction")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modal_chat)

        fragment = ChatFragment.newInstance(edna.chatcenter.core.annotation.OpenWay.DEFAULT)

        supportFragmentManager
            .beginTransaction()
            .add(R.id.chatFragmentContainer, getChatFragment())
            .commitAllowingStateLoss()
    }

    private fun getChatFragment(): ChatFragment {
        val chatCenterUI = (applicationContext as? EdnaChatCenterApplication)?.chatCenterUI
        return chatCenterUI?.getChatFragment() ?: ChatFragment.newInstance(edna.chatcenter.core.annotation.OpenWay.DEFAULT)
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatFragment.weakInstance.clear()
    }

    override fun onBackPressed() {
        if (fragment?.onBackPressed() == true) {
            super.onBackPressed()
        }
    }
}
