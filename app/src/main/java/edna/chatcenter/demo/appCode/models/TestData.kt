package edna.chatcenter.demo.appCode.models

import com.google.gson.Gson
import edna.chatcenter.ui.visual.ChatConfig

data class TestData(
    val userInfo: UserInfo? = null,
    val serverConfig: ServerConfig? = null,
    val chatConfig: ChatConfig? = null
) {
    fun toJson() = Gson().toJson(this)

    companion object {
        fun fromJson(json: String) = Gson().fromJson(json, TestData::class.java)
    }
}
