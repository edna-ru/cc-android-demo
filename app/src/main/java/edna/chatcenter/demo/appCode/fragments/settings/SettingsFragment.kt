package edna.chatcenter.demo.appCode.fragments.settings

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import edna.chatcenter.demo.R
import edna.chatcenter.demo.appCode.fragments.BaseAppFragment
import edna.chatcenter.demo.databinding.FragmentSettingsBinding

class SettingsFragment : BaseAppFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSwitchers()
        initRadioButtons()
        setOnClickListeners()
        subscribeToGlobalBackClick()
    }

    override fun navigateUp() {
        findNavController().navigateUp()
    }

    private fun initSwitchers() = getBinding()?.apply {
        viewModel.asyncInitEnabled.observe(viewLifecycleOwner) { isEnabled ->
            asyncInitSwitcher.isChecked = isEnabled
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

        asyncInitSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setAsyncInitEnabled(isChecked)
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

    private fun setOnClickListeners() = getBinding()?.apply {
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
