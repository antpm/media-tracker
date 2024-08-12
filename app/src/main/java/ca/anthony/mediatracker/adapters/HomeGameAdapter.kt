package ca.anthony.mediatracker.adapters

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.models.Game
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Locale

class HomeGameAdapter(private val game: Game, private val image: Uri, private val id: String): RecyclerView.Adapter<HomeGameAdapter.ViewHolder>() {

    //gets context for use in various methods
    private lateinit var context: Context
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        context = recyclerView.context
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeGameAdapter.ViewHolder {
        //inflate the view with recycler view layout
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_home_latest_game, parent, false)
        return HomeGameAdapter.ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HomeGameAdapter.ViewHolder, position: Int) {
        holder.gameTitle.text = game.title

        Glide.with(context).load(image).into(holder.gameImage)


        val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        holder.gameComplete.text = context.getString(R.string.complete_date, format.format(game.complete!!))

        holder.gameRating.text = context.getString(R.string.rating, game.rating)

        holder.gameDetailsButton.setOnClickListener{
            val bundle = Bundle()
            bundle.putSerializable("game", game)
            bundle.putString("id", id)
            Navigation.findNavController(holder.itemView).navigate(R.id.action_home_fragment_to_game_detail_fragment, bundle)

        }
    }

    override fun getItemCount(): Int {
        return 1
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val gameImage: ImageView = itemView.findViewById(R.id.HomeGameImage)
        val gameTitle: TextView = itemView.findViewById(R.id.HomeGameTitle)
        val gameRating: TextView = itemView.findViewById(R.id.HomeGameRating)
        val gameComplete: TextView = itemView.findViewById(R.id.HomeGameCompleteDate)
        val gameDetailsButton: Button = itemView.findViewById(R.id.HomeGameViewDetailsButton)
    }
}