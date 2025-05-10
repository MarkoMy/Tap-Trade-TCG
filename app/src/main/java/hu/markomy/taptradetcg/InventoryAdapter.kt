package hu.markomy.taptradetcg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InventoryAdapter(
    private var cards: List<Pair<Card, Int>>
) : RecyclerView.Adapter<InventoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardImage: ImageView = view.findViewById(R.id.cardImage)
        val cardCount: TextView = view.findViewById(R.id.cardCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.inventory_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = cards.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (card, count) = cards[position]
        CardManager.loadCardImage(holder.itemView.context, card, holder.cardImage)
        holder.cardCount.text = "x$count"
    }

    fun updateData(newCards: List<Pair<Card, Int>>) {
        cards = newCards
        notifyDataSetChanged()
    }
}