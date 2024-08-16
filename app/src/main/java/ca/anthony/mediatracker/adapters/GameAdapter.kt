package ca.anthony.mediatracker.adapters

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.models.Game
import java.text.SimpleDateFormat
import java.util.Locale

class GameAdapter(private val gameList: ArrayList<Game>, private val gameIDList: ArrayList<String>): RecyclerView.Adapter<GameAdapter.ViewHolder>() {

    //gets context for use in various methods
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
        val id = gameIDList[position]

        //set simple text to fields
        holder.gameTitle.text = game.title
        holder.gamePlatform.text = context?.getString(R.string.platform, game.platform)

        //set rating field to the rating number string and insert rating number from game
        holder.gameRating.text = context?.getString(R.string.rating, game.rating)

        //convert date from long and format into string
        val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        holder.gameDate.text = context?.getString(R.string.complete_date, format.format(game.complete!!))

        //set button listener
        holder.gameButton.setOnClickListener{
            val bundle = Bundle()
            bundle.putSerializable("game", game)
            bundle.putString("id", id)
            Navigation.findNavController(holder.itemView).navigate(R.id.action_game_fragment_to_game_detail_fragment, bundle)

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
        val gameButton: Button = itemView.findViewById(R.id.GameListDetailButton)
    }

}