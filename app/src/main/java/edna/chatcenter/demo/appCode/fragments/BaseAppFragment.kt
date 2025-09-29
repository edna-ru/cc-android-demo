package edna.chatcenter.demo.appCode.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import edna.chatcenter.demo.R
import edna.chatcenter.demo.appCode.fragments.demoSamplesFragment.DemoSamplesFragment
import edna.chatcenter.demo.appCode.fragments.demoSamplesList.DemoSamplesListFragment
import edna.chatcenter.demo.integrationCode.EdnaChatCenterApplication
import edna.chatcenter.ui.visual.core.ChatCenterUI
import java.lang.ref.SoftReference
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams as CLP

abstract class BaseAppFragment<T : ViewBinding>(
    private val bindingInflater: (layoutInflater: LayoutInflater) -> T
) : Fragment() {
    protected val chatCenterUI: ChatCenterUI?
        get() {
            return (context?.applicationContext as? EdnaChatCenterApplication)?.chatCenterUI
        }
    internal var binding: SoftReference<T>? = null
    private val statusScrimTag = "ec_status_bar_scrim"

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
        setApi35Insets()
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

    private fun setApi35Insets() = getBinding()?.apply {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            val toolbar = try {
                root.findViewById<View>(R.id.toolbar)
            } catch (ignored: Exception) {
                null
            }
            toolbar?.apply {
                ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
                    val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                    view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                        topMargin = insets.top
                    }
                    windowInsets
                }

                addStatusBarScrimAboveToolbar(
                    toolbar = toolbar,
                    bgColorRes = R.color.dark_main,
                    darkBackground = true
                )
            }
        }
    }

    protected fun getBinding() = binding?.get()

    private fun addStatusBarScrimAboveToolbar(
        toolbar: View,
        @ColorRes bgColorRes: Int,
        darkBackground: Boolean
    ) {
        val parent = toolbar.parent as? ViewGroup ?: return

        WindowInsetsControllerCompat(requireActivity().window, requireView()).isAppearanceLightStatusBars =
            !darkBackground

        val existing = parent.children()
            .firstOrNull { it.tag == statusScrimTag }
            ?: View(requireContext()).apply {
                tag = statusScrimTag
                elevation = (toolbar.elevation + 1f).coerceAtLeast(1f)

                val index = parent.indexOfChild(toolbar)
                if (parent is ConstraintLayout) {
                    id = View.generateViewId()
                    parent.addView(
                        this,
                        index,
                        CLP(CLP.MATCH_CONSTRAINT, 0).apply {
                            startToStart = CLP.PARENT_ID
                            endToEnd = CLP.PARENT_ID
                            topToTop = CLP.PARENT_ID
                        }
                    )
                } else {
                    parent.addView(
                        this,
                        index,
                        ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            0
                        )
                    )
                }
            }

        existing.setBackgroundColor(ContextCompat.getColor(requireContext(), bgColorRes))

        val applyInsets: (WindowInsetsCompat) -> Unit = { insets ->
            val top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            if (parent is ConstraintLayout) {
                existing.updateLayoutParams<CLP> { height = top }
            } else {
                existing.updateLayoutParams<ViewGroup.LayoutParams> { height = top }
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(parent) { _, insets ->
            applyInsets(insets)
            insets
        }
        ViewCompat.requestApplyInsets(parent)
    }

    private fun ViewGroup.children(): Sequence<View> = sequence {
        for (i in 0 until childCount) yield(getChildAt(i))
    }
}
