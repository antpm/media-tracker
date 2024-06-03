package ca.anthony.mediatracker.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.activities.GameDetailActivity
import ca.anthony.mediatracker.models.Game
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GameAdapter(private val gameList: ArrayList<Game>): RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    private var context: Context? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameAdapter.ViewHolder {
        //inflate the view with recycler view layout
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_game_list, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GameAdapter.ViewHolder, position: Int) {
        //get game from list
        val game = gameList[position]

        //set simple text to fields
        holder.gameTitle.text = game.title
        holder.gamePlatform.text = game.platform

        //set rating field to the rating number string and insert rating number from game
        holder.gameRating.text = context?.getString(R.string.recycler_game_rating_number, game.rating)

        //convert date from long and format into string
        val date = Date(game.complete!!)
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.US)
        holder.gameDate.text = format.format(date)

        //set button listener
        holder.gameButton.setOnClickListener{
            val intent = Intent(context, GameDetailActivity::class.java)
            intent.putExtra("game", game)
            startActivity(context!!, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return gameList.size
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val gameTitle: TextView = itemView.findViewById(R.id.GameTitle)
        val gamePlatform: TextView = itemView.findViewById(R.id.GamePlatform)
        val gameDate: TextView = itemView.findViewById(R.id.GameCompleteDate)
        val gameRating: TextView = itemView.findViewById(R.id.GameRating)
        val gameButton: Button = itemView.findViewById(R.id.GameButton)
    }

}