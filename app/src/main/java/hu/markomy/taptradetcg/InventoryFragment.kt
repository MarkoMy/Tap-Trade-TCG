package hu.markomy.taptradetcg

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class InventoryFragment : Fragment() {
    private lateinit var adapter: InventoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.inventory_fragment, container, false)
    }

    private fun loadInventory() {
        lifecycleScope.launch {
            val cardDao = AppDatabase.getInstance(requireContext()).cardDao()
            val playerCards = cardDao.getAll()
            val allCards = CardManager.getAllCards()
            val inventory = playerCards.mapNotNull { pc ->
                val card = allCards.find { it.id == pc.cardId }
                if (card != null) Pair(card, pc.count) else null
            }
            Log.d("InventoryFragment", "playerCards: $playerCards")
            Log.d("InventoryFragment", "inventory: $inventory")
            adapter.updateData(inventory)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.inventoryRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = InventoryAdapter(emptyList()) { card, _ ->
            showCardInfoDialog(card)
        }
        recyclerView.adapter = adapter

        loadInventory()
    }

    private fun showCardInfoDialog(card: Card) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_card_info, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.dialogCardImage)
        val nameView  = dialogView.findViewById<TextView>(R.id.dialogCardName)
        val rarityView = dialogView.findViewById<TextView>(R.id.dialogCardRarity)

        // load image and set texts
        CardManager.loadCardImage(requireContext(), card, imageView)
        nameView.text = getString(R.string.card_name, card.name)
        rarityView.text = getString(R.string.card_rarity, card.rarity)

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadInventory()
    }
}