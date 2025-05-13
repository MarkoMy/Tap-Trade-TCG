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
        addCard(24, "Banana Man", "024.png", "Yellow")
        addCard(25, "Bayou Fountain", "025.png", "Gray")
        addCard(26, "Bayou Surge", "026.png", "Gray")
        addCard(27, "Beach Ball", "027.png", "Rainbow")
        addCard(28, "Beach Blast", "028.png", "Rainbow")
        addCard(29, "Beach Mummy", "029.png", "Yellow")
        addCard(30, "Bean Ball Bomba", "030.png", "Pink")
        addCard(31, "Berserk Bakery", "031.png", "Green")
        addCard(32, "Betty", "032.png", "Yellow")
        addCard(33, "Big Foot", "033.png", "Rainbow")
        addCard(34, "Biomancer", "034.png", "Blue")
        addCard(35, "Bitterchill Avalancheetah", "035.png", "Blue")
        addCard(36, "Black Hole Pendant", "036.png", "Rainbow")
        addCard(37, "Blizzardpaw Avalancheetah", "037.png", "Blue")
        addCard(38, "Blonde MerWitch", "038.png", "Rainbow")
        addCard(39, "Blood Bath", "039.png", "Gray")
        addCard(40, "Blood Castle", "040.png", "Rainbow")
        addCard(41, "Blood Fortress", "041.png", "Rainbow")
        addCard(42, "Blood Transfusion", "042.png", "Rainbow")
        addCard(43, "Blue Candy", "043.png", "Pink")
        addCard(44, "Blue Plains Drifter", "044.png", "Blue")
        addCard(45, "Blueberry Djinni", "045.png", "Blue")
        addCard(46, "Boarder Collie", "046.png", "Blue")
        addCard(47, "Bog Ban-She Angel", "047.png", "Gray")
        addCard(48, "Bog Bum", "048.png", "Gray")
        addCard(49, "Bog Cyclone", "049.png", "Gray")
        addCard(50, "Bog Frog Bomb", "050.png", "Gray")
        addCard(51, "Bog Palace", "051.png", "Gray")
        addCard(52, "Bomb Pop", "052.png", "Blue")
        addCard(53, "Bone Wand", "053.png", "Rainbow")
        addCard(54, "Bongo Bounce House", "054.png", "Yellow")
        addCard(55, "Bottlebeard, the Cool", "055.png", "Yellow")
        addCard(56, "Bouncing Zebracorn", "056.png", "Green")
        addCard(57, "Brain Gooey", "057.png", "Blue")
        addCard(58, "Brief Power", "058.png", "Rainbow")
        addCard(59, "Brigadier Banana Split", "059.png", "Blue")
        addCard(60, "Brightheart Nexus", "060.png", "Pink")
        addCard(61, "Brilliant Discovery", "061.png", "Rainbow")
        addCard(62, "Bulkunai, the Leech", "062.png", "Gray")
        addCard(63, "Bull Sigil", "063.png", "Rainbow")
        addCard(64, "Bums Away!", "064.png", "Gray")
        addCard(65, "Burly Lumberjack", "065.png", "Green")
        addCard(66, "Business Dog", "066.png", "Blue")
        addCard(67, "Cabin of Many Woods", "067.png", "Green")
        addCard(68, "Cardboard Mansion", "068.png", "Gray")
        addCard(69, "Careful Shuffle", "069.png", "Green")
        addCard(70, "Caramel Camel", "070.png", "Yellow")
        addCard(71, "Cave of Solitude", "071.png", "Pink")
        addCard(72, "Celestial Castle", "072.png", "Rainbow")
        addCard(73, "Celestial Fortress", "073.png", "Rainbow")
        addCard(74, "Cerebral Bloodstorm", "074.png", "Rainbow")
        addCard(75, "Chalice, the Cruel", "075.png", "Yellow")
        addCard(76, "Chancellor Commendation", "076.png", "Green")
        //adding a test card
        //addCard(24, "Test Card", R.drawable.tap, "Test")
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