package ca.anthony.mediatracker.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.anthony.mediatracker.R
import ca.anthony.mediatracker.adapters.GameAdapter
import ca.anthony.mediatracker.models.Game
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject


class GameFragment : Fragment() {

    val db = Firebase.firestore

    private var gameList: ArrayList<Game> = arrayListOf()
    private lateinit var gameRecycler: RecyclerView
    private var gameAdapter = GameAdapter(gameList)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gameRecycler = view.findViewById(R.id.GameRecycler)
        gameRecycler.layoutManager = LinearLayoutManager(context)
        gameRecycler.adapter = gameAdapter

        loadGames()
    }

    private fun loadGames(){
        val data = db.collection("games").orderBy("complete").get()

        data.addOnSuccessListener {docs ->
            for (doc in docs){
                val game = doc.toObject(Game::class.java)
                gameList.add(game)
            }
            gameAdapter.notifyDataSetChanged()
        }.addOnFailureListener { exception->
            Log.e("Firestore error", exception.message.toString())
        }
    }
}