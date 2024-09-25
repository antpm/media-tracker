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


        //set image
        Glide.with(context).load(image).into(holder.gameImage)

        //set rest of fields
        holder.gameTitle.text = game.title
        holder.gameDev.text = game.developer
        holder.gamePlat.text = game.platform
        holder.gameGenre.text = game.genre

        val format = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        //holder.gameComplete.text = context.getString(R.string.complete_date, format.format(game.complete!!))
        holder.gameComplete.text = format.format(game.complete!!)

        when (game.rating) {
            1-> holder.gameRating.setImageResource(R.drawable.star1)
            2-> holder.gameRating.setImageResource(R.drawable.star2)
            3-> holder.gameRating.setImageResource(R.drawable.star3)
            4-> holder.gameRating.setImageResource(R.drawable.star4)
            5-> holder.gameRating.setImageResource(R.drawable.star5)
        }


    }

    override fun getItemCount(): Int {
        return 1
    }

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val gameImage: ImageView = itemView.findViewById(R.id.HomeGameImage)
        val gameTitle: TextView = itemView.findViewById(R.id.HomeGameTitle)
        val gameDev: TextView = itemView.findViewById(R.id.HomeGameDeveloper)
        val gamePlat: TextView = itemView.findViewById(R.id.HomeGamePlatform)
        val gameGenre: TextView = itemView.findViewById(R.id.HomeGameGenre)
        val gameRating: ImageView = itemView.findViewById(R.id.HomeGameRating)
        val gameComplete: TextView = itemView.findViewById(R.id.HomeGameCompleteDate)
    }
}