package edna.chatcenter.demo.screenshot

import android.content.Context
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.dropbox.dropshots.Dropshots
import com.dropbox.dropshots.ThresholdValidator
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import edna.chatcenter.demo.appCode.activity.MainActivity
import edna.chatcenter.demo.appCode.business.StringsProvider
import edna.chatcenter.demo.assert
import edna.chatcenter.demo.kaspressoSreens.DemoLoginScreen
import edna.chatcenter.demo.kaspressoSreens.DemoSamplesListView
import edna.chatcenter.ui.R
import org.junit.Rule

open class BaseScreenshotTestCase : TestCase() {
    private val context: Context = ApplicationProvider.getApplicationContext()
    protected val stringsProvider = StringsProvider(context)

    private val intent = Intent(ApplicationProvider.getApplicationContext(), MainActivity::class.java)

    @get:Rule
    internal val activityScenarioRule = ActivityScenarioRule<MainActivity>(intent)

    @get:Rule
    val dropshots = Dropshots(
        resultValidator = ThresholdValidator(0.05f)
    )

    protected fun openDemoExample(exampleTextInList: String) {
        DemoLoginScreen {
            demoButton {
                assert("Кнопка демо примеров должна быть видимой и кликабельной", ::isVisible, ::isClickable)
                click()
            }
        }
        DemoSamplesListView {
            sampleListRecyclerView {
                assert("Список демо примеров должен быть видимый") { isVisible() }
                val child = childWith<DemoSamplesListView.SampleListItem> {
                    withDescendant {
                        withText(exampleTextInList)
                    }
                }
                child {
                    descriptionTextView {
                        assert("Текст демо примера: \"$exampleTextInList\" должен быть кликабельный") {
                            isClickable()
                        }
                        click()
                    }
                }
            }
        }
    }

    protected fun scrollToRecyclerViewEnd() {
        val recyclerViewId = R.id.chatItemsRecycler
        var itemCount = 0
        onView(ViewMatchers.withId(recyclerViewId)).perform(object : androidx.test.espresso.ViewAction {
            override fun getConstraints(): org.hamcrest.Matcher<android.view.View>? {
                return org.hamcrest.Matchers.allOf(
                    ViewMatchers.withId(recyclerViewId),
                    ViewMatchers.isAssignableFrom(RecyclerView::class.java),
                    ViewMatchers.isDisplayed()
                )
            }

            override fun getDescription(): String {
                return "get item count from RecyclerView"
            }

            override fun perform(uiController: androidx.test.espresso.UiController?, view: android.view.View?) {
                val recyclerView = view as RecyclerView
                recyclerView.adapter?.let {
                    itemCount = it.itemCount
                }
            }
        })

        if (itemCount > 0) {
            onView(ViewMatchers.withId(recyclerViewId))
                .perform(
                    RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(itemCount - 1)
                )
        } else {
            println("RecyclerView with id $recyclerViewId is empty or adapter is null. Skipping scrollToEnd.")
        }
    }

    protected fun scrollToRecyclerViewMiddle() {
        val recyclerViewId = R.id.chatItemsRecycler
        var itemCount = 0
        onView(ViewMatchers.withId(recyclerViewId)).perform(object : androidx.test.espresso.ViewAction {
            override fun getConstraints(): org.hamcrest.Matcher<android.view.View>? {
                return org.hamcrest.Matchers.allOf(
                    ViewMatchers.withId(recyclerViewId),
                    ViewMatchers.isAssignableFrom(RecyclerView::class.java),
                    ViewMatchers.isDisplayed()
                )
            }

            override fun getDescription(): String {
                return "get item count from RecyclerView"
            }

            override fun perform(uiController: androidx.test.espresso.UiController?, view: android.view.View?) {
                val recyclerView = view as RecyclerView
                recyclerView.adapter?.let {
                    itemCount = it.itemCount
                }
            }
        })

        if (itemCount > 0) {
            val middlePosition = itemCount / 2
            onView(ViewMatchers.withId(recyclerViewId))
                .perform(
                    RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(middlePosition)
                )
        } else {
            println("RecyclerView with id $recyclerViewId is empty or adapter is null. Skipping scrollToMiddle.")
        }
    }

    protected fun testActivityScreenshot() {
        val stackTraceElements = Thread.currentThread().stackTrace
        val callingMethodName = if (stackTraceElements.size > 3) {
            stackTraceElements[3].methodName
        } else {
            "testMethod"
        }
        activityScenarioRule.scenario.onActivity {
            dropshots.assertSnapshot(callingMethodName)
        }
    }
}
