package hu.markomy.taptradetcg

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_cards")
data class PlayerCard(
    @PrimaryKey val cardId: Int,
    val count: Int
)