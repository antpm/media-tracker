package ca.anthony.mediatracker.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.models.Game
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.storage
import java.text.SimpleDateFormat
import java.util.Locale

class HomeFragment : Fragment() {

    private val db = Firebase.firestore
    private val gameStorage = Firebase.storage.reference.child("images/games")

    //game variables
    private lateinit var gameImage: ImageView
    private lateinit var gameTitle: TextView
    private lateinit var gameRating: TextView
    private lateinit var gameComplete: TextView
    private lateinit var gameDetailButton: Button
    private lateinit var game: Game
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameImage = view.findViewById(R.id.HomeGameImage)
        gameTitle = view.findViewById(R.id.HomeGameTitle)
        gameRating = view.findViewById(R.id.HomeGameRating)
        gameComplete = view.findViewById(R.id.HomeGameCompleteDate)
        gameDetailButton = view.findViewById(R.id.HomeGameViewDetailsButton)

        getLatestGame()
    }

    private fun getLatestGame(){
        val data = db.collection("games").orderBy("complete", Query.Direction.DESCENDING).limit(1).get()
        data.addOnSuccessListener {docs ->
            for (doc in docs){
                game = doc.toObject(Game::class.java)
            }

            gameTitle.text = game.title

            val imageRef = gameStorage.child(game.image.toString())
            imageRef.downloadUrl.addOnSuccessListener{
                Glide.with(requireActivity()).load(it).into(gameImage)
            }
            val format = SimpleDateFormat("MMM dd, yyyy", Locale.US)
            gameComplete.text = requireActivity().getString(R.string.game_complete_date, format.format(game.complete!!))

            gameRating.text = context?.getString(R.string.game_rating, game.rating)
        }


    }
}