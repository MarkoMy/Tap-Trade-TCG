package hu.markomy.taptradetcg

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.rule.GrantPermissionRule
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FullFlowTest {

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.NEARBY_WIFI_DEVICES,
        android.Manifest.permission.POST_NOTIFICATIONS,
        android.Manifest.permission.BLUETOOTH_SCAN,
        android.Manifest.permission.BLUETOOTH_ADVERTISE,
        android.Manifest.permission.BLUETOOTH_CONNECT,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    @get:Rule
    val activityRule = ActivityTestRule(LoadingScreen::class.java)

    @Test
    fun fullUserFlow_openPacksAndTapForFreePack() {
        // 1. Enter username
        onView(withId(R.id.usernameInput))
            .perform(typeText("TestUser"), closeSoftKeyboard())
        onView(withId(R.id.btnSaveUsername))
            .perform(click())

        // Tutorial dialog
        onView(withText(R.string.tutorial_title))
            .check(matches(isDisplayed()))
        onView(withText(R.string.tutorial_okay))
            .perform(click())

        // 2. Open initial pack and click through 5 cards
        onView(withId(R.id.nav_packs)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.packImage)).perform(click())
        repeat(5) {
            onView(withText(R.string.card_opened_button)).perform(click())
        }

        // 3. Go to tap screen and click image 100×
        onView(withId(R.id.nav_tap)).perform(click())
        Thread.sleep(500)
        repeat(100) {
            onView(withId(R.id.tapImage)).perform(click())
        }

        // 4. Open the free pack and click through 5 cards
        onView(withId(R.id.nav_packs)).perform(click())
        Thread.sleep(500)
        onView(withId(R.id.packImage)).perform(click())
        repeat(5) {
            onView(withText("Next")).perform(click())
        }
    }
}