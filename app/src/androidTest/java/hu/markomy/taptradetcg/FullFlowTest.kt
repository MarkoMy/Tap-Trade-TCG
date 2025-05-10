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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FullFlowTest {

    @get:Rule
    val activityRule = ActivityTestRule(LoadingScreen::class.java)

    @Test
    fun fullUserFlow_addCardAndShowInventory() {
        // 1. Fill in username and submit
        onView(withId(R.id.usernameInput)).perform(typeText("TestUser"), closeSoftKeyboard())
        onView(withId(R.id.btnSaveUsername)).perform(click())

        // 2. Wait for main screen to load (wait for bottomNavigationView)
        Thread.sleep(1000)
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))

        // 3. Add a card to the database
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.databaseBuilder(context, AppDatabase::class.java, "test-db")
            .allowMainThreadQueries().build()
        val card = PlayerCard(cardId = 1, count = 1)
        kotlinx.coroutines.runBlocking {
            Log.i("FullFlowTest", "Inserting card into database")
            db.cardDao().insert(card)
            val inventory = db.cardDao().getAll()
            assertTrue(inventory.any { it.cardId == 1 && it.count == 1 })
            Log.i("FullFlowTest", "Card inserted successfully")
        }

        // 4. Navigate to Inventory fragment after switching into clicker fragment
        onView(withId(R.id.nav_tap)).perform(click())
        onView(withId(R.id.nav_inventory)).perform(click())

        // 5. Wait to visually confirm
        Thread.sleep(2000)
    }
}