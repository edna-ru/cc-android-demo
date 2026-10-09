package edna.chatcenter.demo.appCode.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserInfo(
    @SerializedName("userId")
    var userId: String? = null,
    @SerializedName("userData")
    var userData: String? = null,
    @SerializedName("signature")
    var signature: String? = null,
    @SerializedName("authorizationHeader")
    var authorizationHeader: String? = null,
    @SerializedName("xAuthSchemaHeader")
    var xAuthSchemaHeader: String? = null,
    @SerializedName("isShowMenu")
    var isShowMenu: Boolean = false,
) : Parcelable {

    override fun toString() = "$userId," +
        "$userData," +
        "$signature," +
        "$authorizationHeader," +
        "$xAuthSchemaHeader," +
        "$isShowMenu"

    fun isAllFieldsFilled(): Boolean {
        return !userId.isNullOrEmpty()
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is UserInfo) {
            return other.userId == userId &&
                other.signature == signature &&
                other.authorizationHeader == authorizationHeader &&
                other.xAuthSchemaHeader == xAuthSchemaHeader &&
                other.userData == userData
        }
        return false
    }

    fun clone(): UserInfo {
        return UserInfo(
            userId,
            userData,
            signature,
            authorizationHeader,
            xAuthSchemaHeader,
            isShowMenu
        )
    }
}
