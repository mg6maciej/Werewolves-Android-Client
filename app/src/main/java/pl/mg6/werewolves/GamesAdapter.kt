package pl.mg6.werewolves

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class GamesAdapter(private val games: List<GameInfoPlayersOnly>) : RecyclerView.Adapter<GamesAdapter.Holder>() {

    override fun getItemCount() = games.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false))
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        (holder.itemView as TextView).text = games[position].names.joinToString()
        holder.itemView.setOnClickListener {
            it.context.startActivity(Intent(it.context, GameActivity::class.java).putExtra("id", games[position].id))
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
