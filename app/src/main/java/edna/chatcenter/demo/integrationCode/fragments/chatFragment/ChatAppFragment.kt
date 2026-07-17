package edna.chatcenter.demo.integrationCode.fragments.chatFragment

import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import edna.chatcenter.demo.R
import edna.chatcenter.demo.appCode.business.UiThemeProvider
import edna.chatcenter.demo.appCode.fragments.BaseAppFragment
import edna.chatcenter.demo.appCode.fragments.StartChatFragment
import edna.chatcenter.demo.appCode.fragments.log.LogFragment
import edna.chatcenter.demo.databinding.FragmentChatBinding
import edna.chatcenter.ui.visual.fragments.ChatFragment
import org.koin.java.KoinJavaComponent.inject

class ChatAppFragment : BaseAppFragment<FragmentChatBinding>(FragmentChatBinding::inflate) {
    private val uiThemeProvider: UiThemeProvider by inject(UiThemeProvider::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets(view)
        initTabs()
        subscribeToGlobalBackClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatFragment.weakInstance.clear()
    }

    override fun needHandleDefaultInsets(): Boolean {
        return false
    }

    override fun needDarkStatusBar(): Boolean {
        val currentTab = getBinding()?.viewPager?.currentItem ?: TAB_INDEX_START

        return when (currentTab) {
            TAB_INDEX_CHAT, TAB_INDEX_LOG -> false
            else -> true
        }
    }

    private fun setupInsets(view: View) {
        // Системные отступы обрабатываются, только если режим edge-to-edge активен — согласованно
        // с вызовом enableEdgeToEdge() в активити. Если режим неактивен, окно не находится в
        // режиме edge-to-edge и отступы обрабатываются платформой.
        if (!settingsStorage.edgeToEdgeMode.isActiveOn()) return

        val tabLayout = binding?.get()?.tabLayout ?: return

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            // systemBars.bottom — стабильная высота навигационного бара (клавиатуру он не включает:
            // на API >= 30 это отдельный тип ime, на API < 30 клавиатура приходит через
            // systemWindowInsets, см. ниже).
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Под областью чата (viewPager) расположены навигационный бар и tabLayout. На их высоту
            // клавиатура не перекрывает чат, поэтому она вычитается из инсета, передаваемого вниз.
            val belowChat = systemBars.bottom + tabLayout.height

            tabLayout.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                bottomMargin = systemBars.bottom
            }

            if (supportsImeInsets()) {
                // API >= 30: ime отделяется от системных баров. Вниз передаётся скорректированный
                // ime, нижний системный инсет обнуляется (его уже учли tabLayout и навигационный
                // бар).
                val ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime())
                val updatedImeBottom = maxOf(0, ime.bottom - belowChat)

                WindowInsetsCompat.Builder(windowInsets)
                    .setInsets(
                        WindowInsetsCompat.Type.systemBars(),
                        Insets.of(systemBars.left, systemBars.top, systemBars.right, 0)
                    )
                    .setInsets(
                        WindowInsetsCompat.Type.ime(),
                        Insets.of(ime.left, ime.top, ime.right, updatedImeBottom)
                    )
                    .build()
            } else {
                // API < 30: insets ime() ненадёжны и не отделяются от системных баров, а
                // setInsets(ime()) не доходит до дочернего фрагмента. Корректируется
                // systemWindowInsets — при adjustResize его нижний инсет включает высоту клавиатуры
                // (ChatFragment SDK на API < 30 читает именно его).
                @Suppress("DEPRECATION")
                val legacyBottom = windowInsets.systemWindowInsets.bottom
                val childBottom = maxOf(0, legacyBottom - belowChat)

                @Suppress("DEPRECATION")
                WindowInsetsCompat.Builder(windowInsets)
                    .setSystemWindowInsets(
                        Insets.of(systemBars.left, systemBars.top, systemBars.right, childBottom)
                    )
                    .build()
            }
        }
    }

    /** Insets `WindowInsetsCompat.Type.ime()` надёжны только начиная с API 30 (как и в SDK). */
    private fun supportsImeInsets(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    private fun initTabs() = getBinding()?.apply {
        viewPager.isUserInputEnabled = false
        viewPager.adapter = TabAdapter(childFragmentManager, lifecycle)
        TabLayoutMediator(tabLayout, viewPager) { _, _ -> }.attach()

        for (i in 0 until tabLayout.tabCount) {
            tabLayout.getTabAt(i)?.view?.setPadding(0, 0, 0, 0)
            tabLayout.getTabAt(i)?.customView = createTabView(i)
        }

        viewPager.currentItem = 0
    }

    private fun createTabView(position: Int): View {
        var normalColor = R.color.light_toolbar
        var selectedColor = R.color.blue_color_dark
        var textColor = ContextCompat.getColor(requireContext(), R.color.alt_threads_chat_context_menu)
        if (uiThemeProvider.isDarkThemeOn()) {
            normalColor = R.color.dark_toolbar
            selectedColor = R.color.blue_color_dark
            textColor = ContextCompat.getColor(requireContext(), R.color.alt_threads_chat_context_menu)
        }
        val view = LayoutInflater.from(context).inflate(R.layout.tab_view, null)
        val tabName: TextView = view.findViewById(R.id.name)
        val bg: FrameLayout = view.findViewById(R.id.rootLayout)
        tabName.setText(getTabNameResId(position))
        tabName.setTextColor(textColor)
        bg.background = getStateListDrawable(normalColor, selectedColor)
        return view
    }

    private fun getTabNameResId(position: Int): Int {
        return when (position) {
            0 -> R.string.ecc_start
            1 -> R.string.ecc_chat
            else -> R.string.ecc_log
        }
    }

    private fun getStateListDrawable(normalColor: Int, selectedColor: Int): StateListDrawable {
        val states = StateListDrawable()
        states.addState(
            IntArray(1) { android.R.attr.state_pressed },
            ColorDrawable(ContextCompat.getColor(requireContext(), selectedColor))
        )
        states.addState(
            IntArray(1) { android.R.attr.state_selected },
            ColorDrawable(ContextCompat.getColor(requireContext(), selectedColor))
        )
        states.addState(
            IntArray(1) { android.R.attr.state_enabled },
            ColorDrawable(ContextCompat.getColor(requireContext(), normalColor))
        )
        return states
    }

    fun changeTab(tabIndex: Int) {
        getBinding()?.viewPager?.currentItem = tabIndex
    }

    inner class TabAdapter(
        val fragmentManager: FragmentManager,
        lifeCycle: Lifecycle
    ) : FragmentStateAdapter(fragmentManager, lifeCycle) {
        private var logFragment: LogFragment = LogFragment()
        private val currentPageIds = mutableListOf<Int>()

        override fun getItemCount(): Int {
            return TABS_COUNT
        }

        override fun createFragment(position: Int): Fragment {
            currentPageIds.add(position)
            return when (position) {
                TAB_INDEX_CHAT -> {
                    val fragment = chatCenterUI?.getChatFragment()
                    fragment ?: ChatFragment.newInstance(edna.chatcenter.core.annotation.OpenWay.DEFAULT)
                }
                TAB_INDEX_LOG -> logFragment
                else -> StartChatFragment()
            }
        }

        // FIXME: TabAdapter должен сам управлять ЖЦ своих child'ов
        override fun containsItem(itemId: Long): Boolean {
            for (id in currentPageIds) if (id.toLong() == itemId) {
                currentPageIds.remove(itemId.toString().toInt())
                clearFragment(id)
                break
            }
            return super.containsItem(itemId)
        }

        private fun clearFragment(fragmentId: Int) {
            val transaction = fragmentManager.beginTransaction()
            val fragment = fragmentManager.findFragmentByTag("f$fragmentId")
            if (fragment != null) {
                transaction.remove(fragment)
            }
            transaction.commitAllowingStateLoss()
        }
    }

    companion object {
        const val TAB_INDEX_START = 0
        const val TAB_INDEX_CHAT = 1
        const val TAB_INDEX_LOG = 2
        const val TABS_COUNT = 3
    }
}
