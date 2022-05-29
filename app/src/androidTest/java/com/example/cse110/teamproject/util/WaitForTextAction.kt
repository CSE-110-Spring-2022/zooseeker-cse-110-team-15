package com.example.cse110.teamproject.util;

import android.view.View
import android.widget.TextView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import androidx.test.espresso.util.HumanReadables
import org.hamcrest.Matcher
import java.util.concurrent.TimeoutException

// https://stackoverflow.com/questions/49796132/android-espresso-wait-for-text-to-appear/49814995#49814995
/**
 * A [ViewAction] that waits up to [timeout] milliseconds for a [View]'s text to change to [text].
 *
 * @param text the text to wait for.
 * @param timeout the length of time in milliseconds to wait for.
 */
class WaitForTextAction(private val text: String,
                        private val timeout: Long) : ViewAction {

        override fun getConstraints(): Matcher<View> {
        return isAssignableFrom(TextView::class.java)
        }

        override fun getDescription(): String {
        return "wait up to $timeout milliseconds for the view to have text $text"
        }

        override fun perform(uiController: UiController, view: View) {
        val endTime = System.currentTimeMillis() + timeout

        do {
        if ((view as? TextView)?.text?.contains(text) == true) return
        uiController.loopMainThreadForAtLeast(50)
        } while (System.currentTimeMillis() < endTime)

        throw PerformException.Builder()
        .withActionDescription(description)
        .withCause(TimeoutException("Waited $timeout milliseconds"))
        .withViewDescription(HumanReadables.describe(view))
        .build()
        }
}

/**
 * @return a [WaitForTextAction] instance created with the given [text] and [timeout] parameters.
 */
fun waitForText(text: String, timeout: Long): ViewAction {
        return WaitForTextAction(text, timeout)
}