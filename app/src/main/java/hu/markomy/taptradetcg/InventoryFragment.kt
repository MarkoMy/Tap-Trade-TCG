package hu.markomy.taptradetcg

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        adapter = InventoryAdapter(emptyList()){ card, count ->
        }
        recyclerView.adapter = adapter

        loadInventory()
    }

    override fun onResume() {
        super.onResume()
        loadInventory()
    }
}