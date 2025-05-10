package hu.markomy.taptradetcg

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class AppDatabaseTest {
    private lateinit var db: AppDatabase
    private lateinit var dao: CardDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.cardDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun insertCardAndInventoryReturnsIt() = runBlocking {
        val card = PlayerCard(cardId = 1, count = 1)
        dao.insert(card)
        val inventory = dao.getAll()
        assertTrue(inventory.any { it.cardId == 1 && it.count == 1 })
    }
}