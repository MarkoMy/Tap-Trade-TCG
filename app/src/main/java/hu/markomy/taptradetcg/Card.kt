package hu.markomy.taptradetcg

data class Card(
    val id: Int,
    val name: String,
    val imageName: String?, // "001.png" or "002.jpg"
    val rarity: String,
    val drawableResId: Int? = null
)
