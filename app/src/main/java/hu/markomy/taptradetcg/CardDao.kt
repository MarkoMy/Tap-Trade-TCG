package hu.markomy.taptradetcg

import androidx.room.*

@Dao
interface CardDao {
    @Query("SELECT * FROM player_cards")
    suspend fun getAll(): List<PlayerCard>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: PlayerCard)

    @Update
    suspend fun update(playerCard: PlayerCard)

    @Query("SELECT * FROM player_cards WHERE cardId = :cardId LIMIT 1")
    suspend fun getPlayerCardByCardId(cardId: Int): PlayerCard?
}