package edna.chatcenter.demo.appCode.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import edna.chatcenter.demo.appCode.business.SettingsStorage
import org.koin.android.ext.android.inject

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {

    private val settingsStorage: SettingsStorage by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Режим edge-to-edge включается согласно настройке приложения и конфигурации SDK.
        if (settingsStorage.edgeToEdgeMode.isActiveOn()) {
            enableEdgeToEdge()
        }

        super.onCreate(savedInstanceState)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
