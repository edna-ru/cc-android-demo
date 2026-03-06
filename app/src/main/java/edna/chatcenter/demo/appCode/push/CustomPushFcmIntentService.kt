package edna.chatcenter.demo.appCode.push

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import edna.chatcenter.demo.integrationCode.EdnaChatCenterApplication
import edna.chatcenter.ui.visual.core.ChatCenterUI

class CustomPushFcmIntentService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        ChatCenterUI.setFCMToken(token, applicationContext)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val application = applicationContext as EdnaChatCenterApplication
        application.chatCenterUI?.handleFCMMessage(message.data)
    }
}
