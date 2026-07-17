package edna.chatcenter.demo.appCode.fragments

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import edna.chatcenter.demo.R
import edna.chatcenter.demo.appCode.fragments.demoSamplesFragment.DemoSamplesFragment
import edna.chatcenter.demo.appCode.fragments.demoSamplesList.DemoSamplesListFragment
import edna.chatcenter.demo.appCode.business.SettingsStorage
import edna.chatcenter.demo.integrationCode.EdnaChatCenterApplication
import edna.chatcenter.ui.visual.core.ChatCenterUI
import org.koin.android.ext.android.inject
import java.lang.ref.SoftReference

abstract class BaseAppFragment<T : ViewBinding>(
    private val bindingInflater: (layoutInflater: LayoutInflater) -> T
) : Fragment() {

    protected val settingsStorage: SettingsStorage by inject()

    protected val chatCenterUI: ChatCenterUI?
        get() {
            return (context?.applicationContext as? EdnaChatCenterApplication)?.chatCenterUI
        }
    internal var binding: SoftReference<T>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SoftReference(bindingInflater.invoke(inflater))
        return getBinding()?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBarsColor()

        if (needHandleDefaultInsets()) {
            setupInsets(view)
        }
    }

    override fun onResume() {
        super.onResume()
        applyStatusBarLightAppearance(needDarkStatusBar())
    }

    protected fun subscribeToGlobalBackClick() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    navigateUp()
                }
            }
        )
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    protected open fun navigateUp() {
        val isDemoListFragment = this is DemoSamplesListFragment || this is DemoSamplesFragment
        if (isDemoListFragment && isAdded) {
            findNavController().navigateUp()
        }
    }

    protected fun setBarsColor() = getBinding()?.apply {
        if (chatCenterUI != null) {
            context?.let { context ->
                val toolbar = try {
                    root.findViewById<Toolbar>(R.id.toolbar)
                } catch (ignored: Exception) {
                    null
                }

                toolbar?.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_main))
            }
        }
    }

    private fun setupInsets(view: View) {
        val toolbar = try {
            view.findViewById<View>(R.id.toolbar)
        } catch (_: Exception) {
            null
        } ?: return


        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            toolbar.updatePadding(top = systemBars.top)

            return@setOnApplyWindowInsetsListener windowInsets
        }
    }

    /**
     * Применяет внешний вид иконок статус-бара с учётом темы и режима edge-to-edge.
     *
     * Используется как при настройке в [onResume] (по [needDarkStatusBar]), так и для экранов,
     * которые задают цвет иконок динамически (например, по выбранной вкладке).
     *
     * @param needDarkStatusBar требуются ли тёмные иконки (для светлого фона). Значение учитывается
     *   только в режиме edge-to-edge: в тёмной теме и в режиме совместимости фон статус-бара тёмный,
     *   поэтому всегда применяются светлые иконки независимо от параметра.
     */
    protected fun applyStatusBarLightAppearance(needDarkStatusBar: Boolean) {
        val isDarkThemeOn = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }
        val isLightStatusBar = when {
            // Тёмная тема: фон статус-бара тёмный (прозрачный над тёмным контентом либо тёмный фон
            // темы), требуются светлые иконки.
            isDarkThemeOn -> false
            // Режим совместимости (edge-to-edge не активен и не навязан платформой): WindowInsets
            // не настроены, фон статус-бара тёмный, требуются светлые иконки.
            !isWindowEdgeToEdge() -> false
            // Режим edge-to-edge: фон статус-бара прозрачный, цвет иконок определяется экраном.
            else -> needDarkStatusBar
        }
        setStatusBarLightAppearance(isLightStatusBar)
    }

    /**
     * Находится ли окно в режиме edge-to-edge: режим edge-to-edge из настроек
     * ([SettingsStorage.edgeToEdgeMode]) активен либо edge-to-edge навязан платформой (Android 15+
     * при `targetSdk >= 35`, когда отмена невозможна).
     */
    private fun isWindowEdgeToEdge(): Boolean {
        val context = context ?: return false
        val forcedByPlatform =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM &&
                context.applicationInfo.targetSdkVersion >= Build.VERSION_CODES.VANILLA_ICE_CREAM
        val edgeToEdgeActive = settingsStorage.edgeToEdgeMode.isActiveOn()

        return edgeToEdgeActive || forcedByPlatform
    }

    private fun setStatusBarLightAppearance(isLight: Boolean) {
        val view = this.view ?: return
        val window = requireActivity().window
        val controller = WindowInsetsControllerCompat(window, view)
        // На API < 30 запись isAppearanceLightStatusBars меняет systemUiVisibility decor-view и
        // инициирует новый проход WindowInsets. Этот метод вызывается часто и повторно (onResume),
        // поэтому пишем только при реальном изменении.
        if (controller.isAppearanceLightStatusBars != isLight) {
            controller.isAppearanceLightStatusBars = isLight
        }
    }

    protected fun getBinding() = binding?.get()

    protected open fun needHandleDefaultInsets(): Boolean = true

    protected open fun needDarkStatusBar(): Boolean = false
}
