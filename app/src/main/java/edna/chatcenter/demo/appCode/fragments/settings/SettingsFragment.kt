package edna.chatcenter.demo.appCode.fragments.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import edna.chatcenter.demo.appCode.fragments.BaseAppFragment
import edna.chatcenter.demo.databinding.FragmentSettingsBinding

class SettingsFragment : BaseAppFragment<FragmentSettingsBinding>(FragmentSettingsBinding::inflate) {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSwitchers()
        setOnClickListeners()
        subscribeToGlobalBackClick()
    }

    private fun initSwitchers() = getBinding()?.apply {
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
    }

    private fun setOnClickListeners() = getBinding()?.apply {
        backButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
