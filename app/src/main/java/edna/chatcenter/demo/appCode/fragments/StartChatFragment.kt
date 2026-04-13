package edna.chatcenter.demo.appCode.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import edna.chatcenter.demo.R
import edna.chatcenter.demo.appCode.activity.ModalChatActivity
import edna.chatcenter.demo.appCode.business.PreferencesProvider
import edna.chatcenter.demo.appCode.business.ServersProvider
import edna.chatcenter.demo.appCode.business.UiThemeProvider
import edna.chatcenter.demo.databinding.FragmentStartChatBinding
import edna.chatcenter.demo.integrationCode.EdnaChatCenterApplication
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.java.KoinJavaComponent

class StartChatFragment() :
    BaseAppFragment<FragmentStartChatBinding>(FragmentStartChatBinding::inflate) {

    private val uiThemeProvider: UiThemeProvider by KoinJavaComponent.inject(UiThemeProvider::class.java)
    private val serversProvider: ServersProvider by inject()
    private val preferences: PreferencesProvider by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets(view)
        initListeners()
        initUnreadCounter()
    }

    override fun onResume() {
        super.onResume()
        initData()
        initView()
    }

    override fun needHandleInsets(): Boolean {
        return false
    }

    override fun needDarkStatusBar(): Boolean {
        return true
    }

    private fun setupInsets(view: View) {
        val logoOriginalMarginTop = binding?.get()?.logo?.marginTop ?: 0

        val applyInsets = { windowInsets: WindowInsetsCompat ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding?.get()?.logo?.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                topMargin = systemBars.top + logoOriginalMarginTop
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(view) { _, windowInsets ->
            applyInsets(windowInsets)
            windowInsets
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val attachStateChangeListener = object : View.OnAttachStateChangeListener {
                override fun onViewAttachedToWindow(attachedView: View) {
                    attachedView.removeOnAttachStateChangeListener(this)
                    ViewCompat.getRootWindowInsets(attachedView)?.let(applyInsets)
                }

                override fun onViewDetachedFromWindow(v: View) {}
            }
            view.addOnAttachStateChangeListener(attachStateChangeListener)
        }
    }

    private fun initListeners() = getBinding()?.apply {
        goToChat.setOnClickListener {
            activity?.let {
                val intent = Intent(it, ModalChatActivity::class.java)
                startActivity(intent)
            } ?: Log.e("StartChatFragment", "context is null")
        }
        logout.setOnClickListener {
            chatCenterUI?.logout()
            findNavController().navigateUp()
        }
        prefill.setOnClickListener {
            chatCenterUI?.prefill("Добрый день! У меня есть вопрос")
        }
    }

    private fun initUnreadCounter() {
        val application = context?.applicationContext as? EdnaChatCenterApplication

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                application?.unreadCountMessagesFlow?.collect { count ->
                    if (count == 0U) {
                        getBinding()?.unreadCountTextView?.visibility = View.GONE
                    } else {
                        val text = "${context?.getString(R.string.unread_messages_count)}: $count"

                        getBinding()?.unreadCountTextView?.visibility = View.VISIBLE
                        getBinding()?.unreadCountTextView?.text = text
                    }
                }
            }
        }
    }

    private fun initData() = getBinding()?.apply {
        about.text = generateAboutText()
    }

    private fun initView() = getBinding()?.apply {
        if (uiThemeProvider.isDarkThemeOn()) {
            goToChat.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_color))
            goToChat.setBackgroundResource(R.drawable.buttons_bg_selector_dark)
        } else {
            goToChat.setTextColor(ContextCompat.getColor(requireContext(), R.color.white_color_fa))
            goToChat.setBackgroundResource(R.drawable.buttons_bg_selector)
        }
    }

    private fun generateAboutText(): String {
        return "Server: ${serversProvider.getSelectedServer()?.name}\n" +
            "User: ${preferences.getSelectedUser()?.userId}\n" +
            "Api Version: ${preferences.getSelectedApiVersion()}"
    }
}
