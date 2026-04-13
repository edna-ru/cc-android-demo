package edna.chatcenter.demo.appCode.business

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate

class UiThemeProvider(private val context: Context) {

    fun isDarkThemeOn(): Boolean {
        val isDarkThemeOn = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> context.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }

        return isDarkThemeOn
    }
}
