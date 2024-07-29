package ca.anthony.mediatracker.fragments

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.adapters.HomeGameAdapter
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
    private var game: Game = Game()
    private var image: Uri = Uri.EMPTY
    private lateinit var gameAdapter: HomeGameAdapter
    private lateinit var gameRecycler: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameRecycler = view.findViewById(R.id.HomeGameRecycler)

        getLatestGame()

    }

    private fun getLatestGame(){
        var id = ""
        val data = db.collection("games").orderBy("complete", Query.Direction.DESCENDING).limit(1).get()
        data.addOnSuccessListener {docs ->
            for (doc in docs){
                game = doc.toObject(Game::class.java)
                id = doc.id
            }

            if (game.title != null){
                val imageRef = gameStorage.child(game.image.toString())
                imageRef.downloadUrl.addOnSuccessListener{
                    image = it

                    gameAdapter = HomeGameAdapter(game, image, id)
                    gameRecycler.layoutManager = LinearLayoutManager(context)
                    gameRecycler.adapter = gameAdapter

                }
            }


        }


    }
}