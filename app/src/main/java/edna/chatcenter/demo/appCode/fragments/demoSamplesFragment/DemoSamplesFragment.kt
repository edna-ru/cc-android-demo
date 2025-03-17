package edna.chatcenter.demo.appCode.fragments.demoSamplesFragment

import android.os.Bundle
import android.view.View
import edna.chatcenter.demo.R
import edna.chatcenter.demo.appCode.fragments.BaseAppFragment
import edna.chatcenter.demo.databinding.FragmentDemoSamplesBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DemoSamplesFragment : BaseAppFragment<FragmentDemoSamplesBinding>(
    FragmentDemoSamplesBinding::inflate
) {
    private val viewModel: DemoSamplesViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToData()
        subscribeToGlobalBackClick()
        viewLifecycleOwner.lifecycle.addObserver(viewModel)
    }

    override fun onDestroy() {
        super.onDestroy()
        chatCenterUI?.logout()
    }

    private fun subscribeToData() {
        viewModel.chatFragmentLiveData.observe(viewLifecycleOwner) {
            childFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, it)
                .commit()
        }
    }
}
