package edna.chatcenter.demo.appCode.fragments.settings

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import edna.chatcenter.demo.R
import edna.chatcenter.demo.appCode.business.EdgeToEdgeMode
import edna.chatcenter.demo.appCode.fragments.BaseAppFragment
import edna.chatcenter.demo.databinding.FragmentSettingsBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : BaseAppFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private val viewModel: SettingsViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupInsets()
        initSwitchers()
        initRadioButtons()
        initEdgeToEdgeModeRadioButtons()
        setOnClickListeners()
        subscribeToGlobalBackClick()
    }

    override fun navigateUp() {
        findNavController().navigateUp()
    }

    override fun needHandleDefaultInsets(): Boolean {
        return false
    }

    private fun setupInsets() {
        val binding = this@SettingsFragment.binding?.get() ?: return

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, windowInsets ->
            val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val systemBottom = systemBars.bottom
            binding.toolbar.updatePadding(top = systemBars.top)
            binding.root.updatePadding(bottom = systemBottom)

            return@setOnApplyWindowInsetsListener windowInsets
        }
    }

    private fun initSwitchers() = getBinding()?.apply {
        viewModel.asyncInitEnabled.observe(viewLifecycleOwner) { isEnabled ->
            asyncInitSwitcher.isChecked = isEnabled
        }
        viewModel.asyncAuthorizationEnabled.observe(viewLifecycleOwner) { isEnabled ->
            asyncAuthorizationSwitcher.isChecked = isEnabled
        }
        viewModel.searchEnabled.observe(viewLifecycleOwner) { isEnabled ->
            searchSwitcher.isChecked = isEnabled
        }
        viewModel.voiceMessagesEnabled.observe(viewLifecycleOwner) { isEnabled ->
            voiceSwitcher.isChecked = isEnabled
        }
        viewModel.openGraphEnabled.observe(viewLifecycleOwner) { isEnabled ->
            openGraphSwitcher.isChecked = isEnabled
        }
        viewModel.keepWebSocketEnabled.observe(viewLifecycleOwner) { isEnabled ->
            webSocketSwitcher.isChecked = isEnabled
        }
        viewModel.keepWebSocketEnabledDuringSession.observe(viewLifecycleOwner) { isEnabled ->
            webSocketDuringSessionSwitcher.isChecked = isEnabled
        }
        viewModel.webSocketReconnectEnabled.observe(viewLifecycleOwner) { isEnabled ->
            webSocketReconnectSwitcher.isChecked = isEnabled
        }
        viewModel.isSdkInitDelayEnabled.observe(viewLifecycleOwner) { isEnabled ->
            sdkInitDelaySwitcher.isChecked = isEnabled
        }
        viewModel.isSubtitleShowOrgUnitEnabled.observe(viewLifecycleOwner) { isEnabled ->
            subtitleShowOrgUnitSwitcher.isChecked = isEnabled
        }
        viewModel.isShowSendMenuOnShakeEnabled.observe(viewLifecycleOwner) { isEnabled ->
            showSendMenuOnShakeSwitcher.isChecked = isEnabled
        }

        asyncInitSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAsyncInitEnabled(isChecked)
        }
        asyncAuthorizationSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAsyncAuthorizationEnabled(isChecked)
        }
        searchSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSearchEnabled(isChecked)
        }
        voiceSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setVoiceMessagesEnabled(isChecked)
        }
        openGraphSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setOpenGraphEnabled(isChecked)
        }
        webSocketSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setKeepWebSocketEnabled(isChecked)
        }
        webSocketDuringSessionSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setKeepWebSocketEnabledDuringSession(isChecked)
        }
        webSocketReconnectSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setWebSocketReconnectEnabled(isChecked)
        }
        sdkInitDelaySwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSdkInitDelayEnabled(isChecked)
        }
        subtitleShowOrgUnitSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setSubtitleShowOrgUnitEnabled(isChecked)
        }
        showSendMenuOnShakeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setShowSendMenuOnShakeEnabled(isChecked)
        }
    }

    private fun initRadioButtons() {
        getBinding()?.apply {
            viewModel.appDayNightMode.observe(viewLifecycleOwner) { dayNightMode ->
                when (dayNightMode) {
                    AppCompatDelegate.MODE_NIGHT_NO -> {
                        dayModeRadioButton.isChecked = true
                    }

                    AppCompatDelegate.MODE_NIGHT_YES -> {
                        nightModeRadioButton.isChecked = true
                    }

                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                        followSystemModeRadioButton.isChecked = true
                    }
                }
            }

            followSystemModeRadioButton.isEnabled = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            dayNightModeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                val mode = when (checkedId) {
                    R.id.dayModeRadioButton -> AppCompatDelegate.MODE_NIGHT_NO
                    R.id.nightModeRadioButton -> AppCompatDelegate.MODE_NIGHT_YES
                    R.id.followSystemModeRadioButton -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    else -> return@setOnCheckedChangeListener
                }

                viewModel.setAppDayNightTheme(mode)

                if (AppCompatDelegate.getDefaultNightMode() != mode) {
                    AppCompatDelegate.setDefaultNightMode(mode)
                }
            }
        }
    }

    private fun initEdgeToEdgeModeRadioButtons() = getBinding()?.apply {
        viewModel.edgeToEdgeMode.observe(viewLifecycleOwner) { mode ->
            val buttonId = when (mode) {
                EdgeToEdgeMode.LEGACY -> R.id.e2eLegacyRadioButton
                EdgeToEdgeMode.FROM_ANDROID_11 -> R.id.e2eFrom11RadioButton
                EdgeToEdgeMode.ALL_VERSIONS -> R.id.e2eAllVersionsRadioButton
            }
            if (edgeToEdgeModeRadioGroup.checkedRadioButtonId != buttonId) {
                edgeToEdgeModeRadioGroup.check(buttonId)
            }
        }

        edgeToEdgeModeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.e2eLegacyRadioButton -> EdgeToEdgeMode.LEGACY
                R.id.e2eFrom11RadioButton -> EdgeToEdgeMode.FROM_ANDROID_11
                R.id.e2eAllVersionsRadioButton -> EdgeToEdgeMode.ALL_VERSIONS
                else -> return@setOnCheckedChangeListener
            }

            if (mode == viewModel.edgeToEdgeMode.value) return@setOnCheckedChangeListener

            viewModel.setEdgeToEdgeMode(mode)
            Toast.makeText(
                requireContext(),
                R.string.edge_to_edge_restart_hint,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setOnClickListeners() = getBinding()?.apply {
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
