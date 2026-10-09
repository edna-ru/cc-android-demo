package edna.chatcenter.demo.appCode.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServerConfig(
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("appMarker")
    var appMarker: String? = null,
    @SerializedName("threadsGateProviderUid")
    var threadsGateProviderUid: String? = null,
    @SerializedName("datastoreUrl")
    var datastoreUrl: String? = null,
    @SerializedName("serverBaseUrl")
    var serverBaseUrl: String? = null,
    @SerializedName("threadsGateUrl")
    var threadsGateUrl: String? = null,
    @SerializedName("isFromApp")
    var isFromApp: Boolean = false,
    @SerializedName("isShowMenu")
    var isShowMenu: Boolean = false,
    @SerializedName("filesAndMediaMenuItemEnabled")
    var filesAndMediaMenuItemEnabled: Boolean = true,
    @SerializedName("trustedSSLCertificates")
    var trustedSSLCertificates: List<Int>? = null,
    @SerializedName("allowUntrustedSSLCertificate")
    var allowUntrustedSSLCertificate: Boolean = false,
    @SerializedName("isInputEnabled")
    var isInputEnabled: Boolean = true,
) : Parcelable {

    override fun toString() = "Server config:\n = $name, " +
        "\nappMarker = $appMarker, " +
        "\nthreadsGateProviderUid = $threadsGateProviderUid, " +
        "\ndatastoreUrl = $datastoreUrl, " +
        "\nserverBaseUrl = $serverBaseUrl, " +
        "\nthreadsGateUrl = $threadsGateUrl, " +
        "\nisFromApp = $isFromApp, " +
        "\nisShowMenu = $isShowMenu, " +
        "\nfilesAndMediaMenuItemEnabled = $filesAndMediaMenuItemEnabled, " +
        "\ntrustedSSLCertificates = $trustedSSLCertificates, " +
        "\nallowUntrustedSSLCertificate = $allowUntrustedSSLCertificate"

    fun isAllFieldsFilled(): Boolean {
        return !name.isNullOrEmpty() &&
            !threadsGateProviderUid.isNullOrEmpty() &&
            !datastoreUrl.isNullOrEmpty() &&
            !serverBaseUrl.isNullOrEmpty() &&
            !threadsGateUrl.isNullOrEmpty()
    }

    fun copy(): ServerConfig {
        return ServerConfig(
            name,
            appMarker,
            threadsGateProviderUid,
            datastoreUrl,
            serverBaseUrl,
            threadsGateUrl,
            isFromApp,
            isShowMenu,
            filesAndMediaMenuItemEnabled,
            trustedSSLCertificates,
            allowUntrustedSSLCertificate
        )
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is ServerConfig) {
            return other.name == name &&
                other.threadsGateProviderUid == threadsGateProviderUid &&
                other.datastoreUrl == datastoreUrl &&
                other.serverBaseUrl == serverBaseUrl &&
                other.threadsGateUrl == threadsGateUrl
        }
        return false
    }
}
