package edna.chatcenter.demo

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

class RecyclerViewMatcher(private val recyclerViewId: Int) {

    fun atPosition(position: Int): Matcher<View> {
        return atPositionOnView(position, -1)
    }

    fun atPositionOnView(position: Int, targetViewId: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Element at position $position in RecyclerView with id $recyclerViewId")
            }

            public override fun matchesSafely(view: View): Boolean {
                val recyclerView = view.rootView.findViewById<RecyclerView>(recyclerViewId)
                    ?: return false
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                    ?: // элемент ещё не отображается
                    return false
                return if (targetViewId == -1) {
                    view == viewHolder.itemView
                } else {
                    val targetView = viewHolder.itemView.findViewById<View>(targetViewId)
                    view == targetView
                }
            }
        }
    }
}
