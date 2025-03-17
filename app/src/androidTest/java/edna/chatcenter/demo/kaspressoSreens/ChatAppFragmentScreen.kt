package edna.chatcenter.demo.kaspressoSreens

import com.kaspersky.kaspresso.screens.KScreen
import edna.chatcenter.demo.R
import edna.chatcenter.demo.integrationCode.fragments.chatFragment.ChatAppFragment
import io.github.kakaocup.kakao.pager.KViewPager
import io.github.kakaocup.kakao.tabs.KTabLayout

object ChatAppFragmentScreen : KScreen<ChatAppFragmentScreen>() {
    override val layoutId = R.layout.fragment_chat
    override val viewClass: Class<*> = ChatAppFragment::class.java

    val tabLayout = KTabLayout { withId(R.id.tabLayout) }
    val viewPager = KViewPager { withId(R.id.viewPager) }
}
