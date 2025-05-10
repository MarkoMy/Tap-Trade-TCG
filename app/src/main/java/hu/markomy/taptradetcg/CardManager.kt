package hu.markomy.taptradetcg

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

object CardManager {
    private val cards = mutableMapOf<Int, Card>()

    fun initialize(context: Context) {
        // All cards gonna be added later
        addCard(1, "10th Anniversary Backer", "001.png", "Yellow")
        addCard(2, "Abdominal Snowman", "002.png", "Blue")
        addCard(3, "AbneGator", "003.png", "Grey")
        addCard(4, "Abraca Amadeus", "004.png", "Grey")
        addCard(5, "Adorabunny", "005.png", "Pink")
        addCard(6, "Aeromancer", "006.png", "Blue")
        addCard(7, "Albino Eyebat", "007.png", "Pink")
        addCard(8, "Amaizing Avalanche", "008.png", "Green")
        addCard(9, "Ancient Comet", "009.png", "Rainbow")
        addCard(10, "Ancient Healer", "010.png", "Blue")
        addCard(11, "Ancient Scholar", "011.png", "Blue")
        addCard(12, "Angel Heart", "012.png", "Rainbow")
        addCard(13, "Angel of Chocolate", "013.png", "Pink")
        addCard(14, "Angel of Vanilla", "014.png", "Pink")
        addCard(15, "Apple Pieclops", "015.png", "Pink")
        addCard(16, "Archer Dan", "016.png", "Green")
        addCard(17, "Archer Danica", "017.png", "Green")
        addCard(18, "Auto-Plucker", "018.png", "Pink")
        addCard(19, "Baby Finn & Jake", "019.png", "Yellow")
        addCard(20, "Bail Out", "020.png", "Brown")
        addCard(21, "Bale Out", "021.png", "Green")
        addCard(22, "Ban-She Princess", "022.png", "Grey")
        addCard(23, "Ban-She Queen", "023.png", "Grey")
        //adding a test card
        addCard(24, "Test Card", R.drawable.tap, "Test")
    }

    private fun addCard(id: Int, name: String, imageName: String, rarity: String) {
        cards[id] = Card(id, name, imageName, rarity)
    }

    // Overloaded function to add a card with drawable resource ID
    fun addCard(id: Int, name: String, drawableResId: Int, rarity: String) {
        cards[id] = Card(id, name, null, rarity, drawableResId)
    }

    fun getCard(id: Int): Card? = cards[id]

    fun getAllCards(): List<Card> = cards.values.toList()

    fun loadCardImage(context: Context, card: Card, imageView: ImageView) {
        if (card.drawableResId != null) {
            imageView.setImageResource(card.drawableResId)
        } else if (card.imageName != null) {
            Glide.with(context)
                .load("file:///android_asset/cards/${card.imageName}")
                .into(imageView)
        } else {
            imageView.setImageResource(android.R.color.darker_gray) // fallback
        }
    }
}