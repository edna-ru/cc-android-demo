package edna.chatcenter.demo.appCode.business

import android.content.Context
import edna.chatcenter.demo.R

class StringsProvider(context: Context) {
    val textMessages = context.getString(R.string.text_messages)
    val connectionErrors = context.getString(R.string.connection_errors)
    val voiceMessages = context.getString(R.string.voice_messages)
    val images = context.getString(R.string.images)
    val files = context.getString(R.string.files)
    val systemMessages = context.getString(R.string.system_messages)
    val chatWithBot = context.getString(R.string.chat_with_bot)
    val chatWithEditAndDeletedMessages = context.getString(R.string.deleted_and_edited_messages)
    val requiredField = context.getString(R.string.required_field)
}
