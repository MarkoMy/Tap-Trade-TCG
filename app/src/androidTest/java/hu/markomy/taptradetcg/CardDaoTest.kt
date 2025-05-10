package hu.markomy.taptradetcg

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CardDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var cardDao: CardDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        cardDao = database.cardDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveCard() = runBlocking {
        val card = PlayerCard(cardId = 1, count = 5)
        cardDao.insert(card)

        val retrievedCards = cardDao.getAll()
        assertEquals(1, retrievedCards.size)
        assertEquals(card.cardId, retrievedCards[0].cardId)
        assertEquals(card.count, retrievedCards[0].count)
    }

    @Test
    fun updateCard() = runBlocking {
        val card = PlayerCard(cardId = 1, count = 5)
        cardDao.insert(card)

        val updatedCard = card.copy(count = 10)
        cardDao.update(updatedCard)

        val retrievedCards = cardDao.getAll()
        assertEquals(1, retrievedCards.size)
        assertEquals(updatedCard.count, retrievedCards[0].count)
    }

    @Test
    fun insertMultipleCards() = runBlocking {
        val card1 = PlayerCard(cardId = 1, count = 5)
        val card2 = PlayerCard(cardId = 2, count = 3)
        cardDao.insert(card1)
        cardDao.insert(card2)

        val retrievedCards = cardDao.getAll()
        assertEquals(2, retrievedCards.size)
    }
}