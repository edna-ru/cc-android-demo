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
import edna.chatcenter.demo.integrationCode.EdnaChatCenterApplication
import edna.chatcenter.ui.visual.core.ChatCenterUI
import java.lang.ref.SoftReference

abstract class BaseAppFragment<T : ViewBinding>(
    private val bindingInflater: (layoutInflater: LayoutInflater) -> T
) : Fragment() {

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

        if (needHandleInsets()) {
            setupInsets(view)
        }
    }

    override fun onResume() {
        super.onResume()
        setupStatusBarLightAppearance(requireView())
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

    private fun setupStatusBarLightAppearance(view: View) {
        val isApi35 = Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        val isDarkThemeOn = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        }
        val isLightStatusBar = when {
            isDarkThemeOn -> false
            /* WindowInsets не настроены, всегда темный статус бар */
            isApi35.not() -> false
            else -> needDarkStatusBar()
        }
        val window = requireActivity().window
        WindowInsetsControllerCompat(window, view).isAppearanceLightStatusBars = isLightStatusBar
    }

    protected fun getBinding() = binding?.get()

    protected open fun needHandleInsets(): Boolean = true

    protected open fun needDarkStatusBar(): Boolean = false
}
