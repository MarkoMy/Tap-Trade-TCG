package hu.markomy.taptradetcg

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import android.app.AlertDialog
import kotlinx.coroutines.launch

class PackOpeningFragment : Fragment() {
    private lateinit var openPackImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.packopening_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        openPackImage = view.findViewById(R.id.packImage)

        openPackImage.setOnClickListener {
            if (PackManager.packsCount <= 0) {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.no_packs_title))
                    .setMessage(getString(R.string.no_packs_message))
                    .setPositiveButton("OK", null)
                    .show()
                return@setOnClickListener
            }

            PackManager.openPack()
            (activity as? MainScreen)?.updatePacksCount()

            val totalCardCount = CardManager.getAllCards().size
            val randomCardIds = List(5) { (1..totalCardCount).random() }

            val db = AppDatabase.getInstance(requireContext())
            val cardDao = db.cardDao()
            lifecycleScope.launch {
                randomCardIds.forEach { cardId ->
                    val existing = cardDao.getPlayerCardByCardId(cardId)
                    if (existing != null) {
                        cardDao.update(existing.copy(count = existing.count + 1))
                    } else {
                        cardDao.insert(PlayerCard(cardId = cardId, count = 1))
                    }
                }
            }

            showCardsSequentially(randomCardIds)
        }
    }

    fun showCardsSequentially(cardIds: List<Int>, index: Int = 0) {
        if (index >= cardIds.size) return

        val card = CardManager.getCard(cardIds[index])
        val dialogView = layoutInflater.inflate(R.layout.dialog_card, null)
        val cardImage = dialogView.findViewById<ImageView>(R.id.cardImage)
        val cardName = dialogView.findViewById<TextView>(R.id.cardName)

        if (card != null) {
            CardManager.loadCardImage(requireContext(), card, cardImage)
            cardName.text = card.name
        } else {
            cardImage.setImageResource(android.R.color.darker_gray)
            cardName.text = "Unknown Card"
        }

        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.card_opened_title))
            .setView(dialogView)
            .setPositiveButton(R.string.card_opened_button) { dialog, _ ->
                dialog.dismiss()
                showCardsSequentially(cardIds, index + 1)
            }
            .setCancelable(false)
            .show()
    }
}